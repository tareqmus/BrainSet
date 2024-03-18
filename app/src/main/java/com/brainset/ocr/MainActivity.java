package com.brainset.ocr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 123;
    //Widgets

    TextView textView;
    Button imageBTN, speechBTN, clearBTN, copyBTN, pauseBTN;

    //Variables
    InputImage inputImage;
    TextRecognizer recognizer;
    TextToSpeech textToSpeech;
    public Bitmap textImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        imageBTN = findViewById(R.id.choose_image);

        textView = findViewById(R.id.text);
        speechBTN = findViewById(R.id.speech);
        clearBTN = findViewById(R.id.clear);
        copyBTN = findViewById(R.id.copy);
        pauseBTN = findViewById(R.id.pause);




        imageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        speechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        copyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from textView
                String text = textView.getText().toString();
                // Use getSystemService to get the ClipboardManager service
                ClipboardManager clipboardManager =(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // Create a new ClipData with the text to copy
                ClipData clipData = ClipData.newPlainText("Text", text);
                // Set the clip to the clipboard
                clipboardManager.setPrimaryClip(clipData);
                // Show a toast message to indicate the text was copied
                Toast.makeText(MainActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the text from textView
                textView.setText("");
                textToSpeech.shutdown();
            }
        });
        pauseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textToSpeech != null){
                    textToSpeech.stop();
                    // Optionally, you can also use textToSpeech.shutdown() if you want to completely release the TextToSpeech resources.
                    // However, you'll need to initialize it again before using textToSpeech.speak().
                }
            }
        });

    }

    //How we to choose the functionality to open our gallery and allow user to select image to start scanning
    private void OpenGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE) {

            if(data != null) {

                byte[] byteArray = new byte[0];
                String filePath = null;

                try {
                    try {
                        inputImage = InputImage.fromFilePath(this, data.getData());
                        Bitmap resultUri = inputImage.getBitmapInternal();



                        // Process the Image
                        Task<Text> result =
                                recognizer.process(inputImage)
                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text text) {
                                                ProcessTextBlock(text);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
    private void ProcessTextBlock(Text text) {
        //Start ML Kit - Process Text Block

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
    @Override
    protected void onPause(){
        if(!textToSpeech.isSpeaking()){
            super.onPause();
        }
    }
}