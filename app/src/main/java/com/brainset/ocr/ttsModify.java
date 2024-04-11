package com.brainset.ocr;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class ttsModify extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Button speak;
    private ImageView backArrow;
    private TextToSpeech engine;
    private EditText editText;
    private SeekBar seekPitch, seekSpeed;
    private float pitchRate = 1f, speedRate = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FocusMode.checkFocusMode(this);
        setContentView(R.layout.tweak_tts);

        speak = (Button) findViewById(R.id.speak);
        editText = (EditText) findViewById(R.id.sentence);
        seekSpeed = (SeekBar) findViewById(R.id.seekSpeed);
        seekPitch = (SeekBar) findViewById(R.id.seekPitch);
        backArrow = findViewById(R.id.backArrow);

        engine = new TextToSpeech(this, this);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ttsModify.this, Gallery.class);
                startActivity(intent);
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        seekPitch.setThumbOffset(5);
        seekPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitchRate = ((float) progress) / 100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekSpeed.setThumbOffset(5);
        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedRate = ((float) progress) / 100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void speak() {
        engine.setPitch(pitchRate);
        engine.setSpeechRate(speedRate);
        engine.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
        Toast.makeText(this, "Your device is speaking...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            engine.setLanguage(Locale.GERMAN);
        }

    }

}
