package com.brainset.ocr;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.brainset.ocr.dao.Scans;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

// Gallery class extends AppCompatActivity to have access to Android lifecycle methods and UI elements.
public class Gallery extends AppCompatActivity {

    // A constant to identify the request code for picking an image
    private static final int PICK_IMAGE = 123;
    private static final  int REQUEST_CODE = 22;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private int lastSpokenIndex = -1; // Initialize to -1 indicating no segment has been spoken yet.
    private String[] textSegments;
    GlobalData gd = new GlobalData();
    FbData db = new FbData();
    // Declaration of UI components
    EditText editText; // Display text extracted or any message
    Button imageBTN, speechBTN, clearBTN, pauseBTN, focusBTN, saveBTN, resumeBTN, buttonCancel, buttonSave; // Buttons for various functionalities

    Dialog dialog;

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
        //focusBTN = findViewById(R.id.focus_mode);
        saveBTN = findViewById(R.id.saveButton);
        pauseBTN = findViewById(R.id.pause);
        resumeBTN = findViewById(R.id.resume);


        // Setting onClick listeners for buttons to handle user interactions

        resumeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSpokenIndex >= 0 && lastSpokenIndex + 1 < textSegments.length) {
                    applyTTSConfig(); // Apply pitch and speed settings from SharedPreferences
                    // Start speaking from the next segment
                    playAndHighlightText(textSegments, lastSpokenIndex + 1);
                }
            }
        });
        pauseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                    // Do not reset lastSpokenIndex here. It should retain the value of the last spoken segment.
                }
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if an image has been selected
                if (inputImage != null) {
                    // Create the dialog
                    Dialog dialog = new Dialog(Gallery.this);
                    dialog.setContentView(R.layout.save_popup);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.save_popupp));
                    dialog.setCancelable(false);

                    // Find views in the dialog layout
                    EditText eScanName = dialog.findViewById(R.id.editTextSaveName);
                    Button buttonSave = dialog.findViewById(R.id.buttonSave);
                    Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the scan name from the EditText
                            String scanName = eScanName.getText().toString();
                            // Get the bitmap from inputImage
                            Bitmap image = inputImage.getBitmapInternal();
                            // Save the bitmap to a file
                            File imageFile = saveBitmap(image, scanName);

                            // Create a new Scans object
                            Scans scan = new Scans(scanName, imageFile);
                            // Update user's scans
                            gd.user.scans.put(scanName, scan);
                            db.setUserScans(gd.user, gd.user.scans);
                            // Save the scan
                            gd.user.scans.get(scanName).save(scanName);

                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    });

                    // Show the dialog
                    dialog.show();
                }
            }
        });


        imageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Method call to open the gallery and choose an image
                showBottomDialog();
            }
        });

        speechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullText = editText.getText().toString();
                String[] sentences = fullText.split("\\. ");
                lastSpokenIndex = -1; // Reset here as well
                applyTTSConfig(); // Apply pitch and speed settings from SharedPreferences
                playAndHighlightText(sentences, 0);
            }
        });

        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(""); // Clears the textView
                lastSpokenIndex = -1; // Reset the last spoken index
                // Stops tts from speaking and clears resources
                if (textToSpeech != null) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                }
            }
        });


        // Initializes the TextToSpeech engine
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    applyTTSConfig(); // Apply pitch and speed settings after initialization
                    // Setup UtteranceProgressListener here
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            int nextIndex = Integer.parseInt(utteranceId) + 1;
                            lastSpokenIndex = Integer.parseInt(utteranceId); // Update lastSpokenIndex
                            if (nextIndex < textSegments.length) {
                                runOnUiThread(() -> playAndHighlightText(textSegments, nextIndex));
                            }

                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Handle TTS errors here
                        }
                    });
                }
            }
        });

    }

    private void applyTTSConfig() {
        SharedPreferences prefs = getSharedPreferences("TTSConfig", MODE_PRIVATE);
        float pitch = prefs.getFloat("pitchRate", 1.0f); // Default pitch rate
        float speed = prefs.getFloat("speedRate", 1.0f); // Default speed rate

        if (textToSpeech != null) {
            textToSpeech.setPitch(pitch);
            textToSpeech.setSpeechRate(speed);
        }
    }


    private void playAndHighlightText(String[] segments, int index) {
        textSegments = segments; // Update the class member variable
        if (index >= segments.length) return; // Stop condition

        String segment = segments[index];
        // Unique ID for each segment
        String utteranceId = String.valueOf(index);

        // Start speaking
        textToSpeech.speak(segment, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

        // Highlight text
        highlightText(segment);
    }

    private void highlightText(String textToHighlight) {
        String fullText = editText.getText().toString();
        int start = fullText.indexOf(textToHighlight);
        if (start >= 0) {
            int end = start + textToHighlight.length();
            SpannableString spannableString = new SpannableString(fullText);
            // Example highlighting using background color. Customize as needed.
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(spannableString);
        }
    }
    private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (textToSpeech != null && textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                    // Optionally, also reset your TTS playback state here, similar to what you do in the pause button's onClickListener.
                }
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        unregisterReceiver(screenOffReceiver);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
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

    private void showBottomDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetdash,null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        LinearLayout galleryLayout = view.findViewById(R.id.bottomsheetGallery);
        LinearLayout cameraLayout = view.findViewById(R.id.bottomsheetCamera);

        galleryLayout.setOnClickListener(v ->{
            bottomSheetDialog.dismiss();
            OpenGallery();
        });

        cameraLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(Gallery.this, Scanner.class);
            startActivity(intent);
        });
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