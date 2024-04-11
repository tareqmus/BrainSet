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
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.brainset.ocr.dao.Scans;
import com.brainset.ocr.dao.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.core.Tag;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

// Gallery class extends AppCompatActivity to have access to Android lifecycle methods and UI elements.
public class Gallery extends AppCompatActivity {

    // A constant to identify the request code for picking an image
    private static final int PICK_IMAGE = 123;
    private static final  int REQUEST_CODE = 22;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    GlobalData gd = new GlobalData();
    FbData db = new FbData();
    // Declaration of UI components
    EditText editText; // Display text extracted or any message
    Button imageBTN, speechBTN, clearBTN, pauseBTN, captureBTN, timerBTN, focusBTN, saveBTN, calendarBTN, tweakttsBTN; // Buttons for various functionalities

    // Declaration of variables for processing
    InputImage inputImage; // Holds the image to process
    ImageView imageView;

    TextRecognizer recognizer; // Recognizes text from images
    TextToSpeech textToSpeech; // Converts text to speech
    public Bitmap textImage; // Holds the bitmap of the selected image
    //method to convert selected image bitmap to File
    public File saveBitmap(Bitmap bitmap, String fileName) {
        File imageFile = null;
        try {
            // Get the default pictures directory
            imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Save the Bitmap to the file
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream); // Change format and quality as needed
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return imageFile;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FocusMode.checkFocusMode(this);
        // Setting the content view to the layout defined in 'activity_main.xml'
        setContentView(R.layout.activity_gallery);

        // Initializing the TextRecognizer with default options
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Linking the UI components to their respective IDs in the layout file
        imageBTN = findViewById(R.id.choose_image);
        editText = findViewById(R.id.editText);
        speechBTN = findViewById(R.id.speech);
        clearBTN = findViewById(R.id.clear);
        captureBTN = findViewById(R.id.capture);
        imageView = findViewById(R.id.imageView);
        timerBTN = findViewById(R.id.study_timer);
        focusBTN = findViewById(R.id.focus_mode);
        saveBTN = findViewById(R.id.saveButton);
        pauseBTN = findViewById(R.id.stop);
        calendarBTN = findViewById(R.id.calendar);
        tweakttsBTN = findViewById(R.id.tweaker);


        // Setting onClick listeners for buttons to handle user interactions
        tweakttsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gallery.this, ttsModify.class);
                startActivity(intent);
            }
        });
        calendarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gallery.this, TaskCalendar.class);
                startActivity(intent);
            }
        });

        pauseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();

                }
            }
        });
        saveBTN.setOnClickListener(new View.OnClickListener() { //button to save scan
            @Override
            public void onClick(View view) {
                //if an image has been selected
                if (inputImage != null){
                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.save_popup, null);

                    // create the popup window
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    //set objects on pop up
                    Button save = popupWindow.getContentView().findViewById(R.id.buttonSave);
                    EditText eScanName = popupWindow.getContentView().findViewById(R.id.editTextSaveName);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String scanName = eScanName.getText().toString();
                            Bitmap image = inputImage.getBitmapInternal();
                            File imageFile = saveBitmap(image, scanName);

                            Scans scan = new Scans(scanName, imageFile);
                            gd.user.scans.put(scanName, scan);
                            db.setUserScans(gd.user, gd.user.scans);
                            gd.user.scans.get(scanName).save(scanName);
                            popupWindow.dismiss();
                        }
                    });
                    // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });

                }
            }
        });
        focusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(Gallery.this, FocusMode.class);
                startActivity(k);
            }
        });
        timerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(Gallery.this, Timer.class);
                startActivity(j);
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
                textToSpeech.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");// Clears the textView and shuts down the TextToSpeech to release resources
                textToSpeech.shutdown(); // Stops tts from speaking
                imageView.setImageResource(0); //Clears imageView
                inputImage = null;
            }
        });

        // Initializes the TextToSpeech engine
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Set<Voice> availableVoices = textToSpeech.getVoices();
                    if (availableVoices != null) {
                        Voice desiredVoice = null;
                        for (Voice voice : availableVoices) {
                            if (voice.getName().equals("es-MX-SMTf00")) {
                                desiredVoice = voice;
                                break;
                            }
                        }
                        if (desiredVoice != null) {
                            textToSpeech.setVoice(desiredVoice);
                            Log.d("TTS", "Voice set to " + desiredVoice.getName());
                        } else {
                            Log.d("TTS", "Desired voice not found, using default.");
                        }
                    }
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
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
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
            editText.append("\n");

            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            for(Text.Line line : block.getLines()){
                String lineText = line.getText();;

                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                for(Text.Element element : line.getElements()){
                    editText.append(" ");
                    String elementText = element.getText();
                    editText.append(elementText);

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
    }

}