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


public class ttsModify extends Fragment implements TextToSpeech.OnInitListener {
    View view;
    Activity a;
    private Button speak;
    private ImageView backArrow;
    private TextToSpeech engine;
    private EditText editText;
    private SeekBar seekPitch, seekSpeed;
    private float pitchRate = 1f, speedRate = 1f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.tweak_tts, container, false);
        a = this.getActivity();
        FocusMode.checkFocusMode(a);


        speak = (Button) view.findViewById(R.id.speak);
        editText = (EditText) view.findViewById(R.id.sentence);
        seekSpeed = (SeekBar) view.findViewById(R.id.seekSpeed);
        seekPitch = (SeekBar) view.findViewById(R.id.seekPitch);
        backArrow = view.findViewById(R.id.backArrow);

        engine = new TextToSpeech(this.getContext(), this);

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
        return view;
    }

    private void speak() {
        engine.setPitch(pitchRate);
        engine.setSpeechRate(speedRate);
        engine.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
        Toast.makeText(this.getContext(), "Your device is speaking...", Toast.LENGTH_SHORT).show();

    }


    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            engine.setLanguage(Locale.CHINESE);
        }

    }

}