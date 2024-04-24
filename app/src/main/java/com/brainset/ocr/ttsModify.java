package com.brainset.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Locale;


// Fragment class to handle text-to-speech (TTS) modifications
public class ttsModify extends Fragment implements TextToSpeech.OnInitListener {
    View view;
    Activity a;
    private Button speak;
    private ImageView backArrow;
    private TextToSpeech engine;
    private EditText editText;
    private SeekBar seekPitch, seekSpeed;
    private float pitchRate = 1f, speedRate = 1f;

    // This method is called when the view for the fragment is created.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tweak_tts, container, false);

        // Reference to the containing Activity
        a = this.getActivity();

        // Initialization function for checking the focus mode settings
        FocusMode.checkFocusMode(a);

        // Hook UI elements to variables
        speak = (Button) view.findViewById(R.id.speak);
        editText = (EditText) view.findViewById(R.id.sentence);
        seekSpeed = (SeekBar) view.findViewById(R.id.seekSpeed);
        seekPitch = (SeekBar) view.findViewById(R.id.seekPitch);
        backArrow = view.findViewById(R.id.backArrow);

        // Initialize TextToSpeech engine
        engine = new TextToSpeech(this.getContext(), this);

        // Setup a click listener for the 'speak' button to invoke the speak method
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        // Adjust the thumb offset for better usability
        seekPitch.setThumbOffset(5);
        // Listener to change the pitch based on user interaction with the seek bar
        seekPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitchRate = ((float) progress) / 100f; // Convert progress to a float scale
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Could add functionality when user starts touching the bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Could add functionality when user stops touching the bar
            }
        });

        // Similar adjustments and listener setup for speed control
        seekSpeed.setThumbOffset(5);
        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedRate = ((float) progress) / 100f; // Convert progress to a float scale
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Could add functionality when user starts touching the bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Could add functionality when user stops touching the bar
            }
        });
        return view;
    }

    // Method to execute text-to-speech conversion
    private void speak() {
        engine.setPitch(pitchRate); // Set pitch level
        engine.setSpeechRate(speedRate); // Set speed rate
        engine.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null); // Speak the text
        Toast.makeText(this.getContext(), "Your device is speaking...", Toast.LENGTH_SHORT).show(); // Show user feedback
    }

    // Method called when the TextToSpeech engine is initialized
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            engine.setLanguage(Locale.US); // Set the language to US English
        }
    }
}
