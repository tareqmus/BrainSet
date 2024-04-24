package com.brainset.ocr;

// Import statements to include necessary Android libraries and third-party packages
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

// MainActivity which serves as the main entry point of the app
public class Scanner extends AppCompatActivity {

    // Declare variables for the UI components and OCR elements
    InputImage inputImage;
    TextRecognizer recognizer;
    TextToSpeech textToSpeech;
    private ImageView captureIV;
    private TextView resultTV;
    private Button snapBtn, detectBtn, speechBtn, clearBtn;
    private Bitmap imageBitmap;

    // Constants to manage image capture intents
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Bind UI elements with their IDs
        captureIV = findViewById(R.id.idIVCaptureImage);
        resultTV = findViewById(R.id.idTVDetectedText);
        snapBtn = findViewById(R.id.idButtonSnap);
        detectBtn = findViewById(R.id.idButtonDetect);
        speechBtn = findViewById(R.id.speech);
        clearBtn = findViewById(R.id.clear);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        // Button to clear the text view and shutdown TextToSpeech
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTV.setText("");
                textToSpeech.shutdown();
                captureIV.setImageResource(0);
            }
        });

        // Button to initiate speech output
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(resultTV.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        // Button to detect text from the captured image
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetectText();
            }
        });

        // Button to capture an image using device camera
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission()) {
                    CaptureImage();
                }else{
                    RequestPermission();
                }
            }
        });
    }

    // Check if camera permission is granted
    private boolean CheckPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA_SERVICE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    // Request camera permission
    private void RequestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE);
    }

    // Intent to capture an image
    private void CaptureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!= null) {
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            CaptureImage();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            captureIV.setImageBitmap(imageBitmap);
        }
    }

    // Process image data to detect text
    private void DetectText() {
        InputImage image = InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder resultText = new StringBuilder();
                for (Text.TextBlock block : text.getTextBlocks()){
                    String blockText = block.getText();
                    resultText.append(blockText).append("\n");
                }
                // Set the text to TextView
                resultTV.setText(resultText.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to Detect Text from Image..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
