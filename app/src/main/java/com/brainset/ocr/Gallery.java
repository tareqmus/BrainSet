package com.brainset.ocr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.core.Tag;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

// Gallery class extends AppCompatActivity to have access to Android lifecycle methods and UI elements.
public class Gallery extends AppCompatActivity {
    // A constant to identify the request code for picking an image
    private static final int PICK_IMAGE = 123;
    private static final  int REQUEST_CODE = 22;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // Declaration of UI components
    TextView textView; // Display text extracted or any message
    Button imageBTN, speechBTN, clearBTN, copyBTN, pauseBTN, captureBTN, timerBTN; // Buttons for various functionalities

    // Declaration of variables for processing
    InputImage inputImage; // Holds the image to process
    ImageView imageView;
    TextRecognizer recognizer; // Recognizes text from images
    TextToSpeech textToSpeech; // Converts text to speech
    public Bitmap textImage; // Holds the bitmap of the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the layout defined in 'activity_main.xml'
        setContentView(R.layout.activity_gallery);

        // Initializing the TextRecognizer with default options
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Linking the UI components to their respective IDs in the layout file
        imageBTN = findViewById(R.id.choose_image);
        textView = findViewById(R.id.text);
        speechBTN = findViewById(R.id.speech);
        clearBTN = findViewById(R.id.clear);
        copyBTN = findViewById(R.id.copy);
        captureBTN = findViewById(R.id.capture);
        imageView = findViewById(R.id.imageView);
        timerBTN = findViewById(R.id.study_timer);

        // Setting onClick listeners for buttons to handle user interactions

        timerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(Gallery.this, Timer.class);
                startActivity(t);
            }
        });
        captureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Gallery.this, Scanner.class);
                startActivity(i);
            }
        });

        imageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Method call to open the gallery and choose an image
                OpenGallery();
            }
        });

        speechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Converts the text in textView to speech
                textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        copyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Copies the text from textView to clipboard
                String text = textView.getText().toString();
                ClipboardManager clipboardManager =(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Gallery.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("");// Clears the textView and shuts down the TextToSpeech to release resources
                textToSpeech.shutdown(); // Stops tts from speaking
                imageView.setImageResource(0); //Clears imageView
            }
        });

        // Initializes the TextToSpeech engine
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);

                }
            }
        });
    }



    // Initializes the process to open the gallery and allows the user to select an image for scanning.
    private void OpenGallery() {
        // Create an intent to get content of type image.
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/");

        // Intent to pick an image from the device's external storage.
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");

        // Combine both intents with a chooser dialog.
        Intent chooserIntent = Intent.createChooser(getIntent, "Select image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        // Start the activity to select an image from the gallery.
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result for the image capture request.
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            imageView.setImageBitmap(image);
        }
        // Handle the result for a generic request code.
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
        // Handle cancellation of the image selection process.
        else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
        // Process the selected image from the gallery.
        if(requestCode == PICK_IMAGE) {
            if(data != null) {
                byte[] byteArray = new byte[0];
                String filePath = null;
                try {
                    try {
                        inputImage = InputImage.fromFilePath(this, data.getData());
                        Bitmap resultUri = inputImage.getBitmapInternal();

                        // Proceed with ML Kit text recognition on the selected image.
                        Task<Text> result = recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text text) {
                                                ProcessTextBlock(text);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Gallery.this, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Extracts and processes the text from the image using ML Kit.
    private void ProcessTextBlock(Text text) {
        // Extracted text is appended to a TextView for display.

        String resultText = text.getText();
        for(Text.TextBlock block : text.getTextBlocks()){

            String blockText = block.getText();
            textView.append("\n");

            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            for(Text.Line line : block.getLines()){
                String lineText = line.getText();;

                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                for(Text.Element element : line.getElements()){
                    textView.append(" ");
                    String elementText = element.getText();
                    textView.append(elementText);

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
    }

}