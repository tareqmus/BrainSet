package com.brainset.ocr;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class Timer extends Fragment {
    TextView countdownTimer;
    Button start, pause, reset, resume, startFocusTime;
    CountDownTimer timer; // Android class to handle countdown functionality
    long timeLeftInMillis; // Stores time left in miliseconds
    boolean focusTimeEnabled = false; // Flag to toggle focus mode
    View view;
    Activity a;

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    NumberPicker secondsPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.study_timer, container, false);
        FocusMode.checkFocusMode(a); // Get a reference to the parent activity
        a = this.getActivity(); // Check and set focus mode based on current settings

        // Initialize UI components
        countdownTimer = view.findViewById(R.id.countdown_timer);
        start = view.findViewById(R.id.start);
        pause = view.findViewById(R.id.pause);
        reset = view.findViewById(R.id.reset);
        resume = view.findViewById(R.id.resume);
        startFocusTime = view.findViewById(R.id.focusTimeBtn);

        // Setup NummberPickers for hours, minutes, and seconds
        hoursPicker = view.findViewById(R.id.hoursPicker);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(99);

        minutesPicker = view.findViewById(R.id.minutesPicker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        secondsPicker = view.findViewById(R.id.secondsPicker);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        // Toggle focus mode button functionality
        startFocusTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startFocusTime.getText().toString().equals("FocusTimeDisabled")) {
                    startFocusTime.setText("FocusTimeEnabled");
                    startFocusTime.setBackgroundColor(Color.parseColor("#15DB4D"));
                    focusTimeEnabled = true;
                    Log.e("ft", focusTimeEnabled + "");
                } else {
                    startFocusTime.setText("FocusTimeDisabled");
                    startFocusTime.setBackgroundColor(Color.parseColor("#FF0000"));
                    focusTimeEnabled = false;
                }
            }
        });

        // Button to start the timer
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel(); // Cancel any existing timer
                }
                long hours = hoursPicker.getValue();
                long minutes = minutesPicker.getValue();
                long seconds = secondsPicker.getValue();
                timeLeftInMillis = (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
                startTime(timeLeftInMillis); // Start a new timer with the specified time
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeTimer();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        return view;
    }

    // Start the timer with the specified duration
    private void startTime(long time) {
        if (focusTimeEnabled) {
            GlobalData.user.inFocusMode = true;
            FocusMode.checkFocusMode(a);
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText(); // Update the timer text
            }

            @Override
            public void onFinish() {
                if (focusTimeEnabled) {
                    FocusMode.exitFocus(a);
                }
                countdownTimer.setText("00:00:00");

                // Notify user time is up and it plays the alarm sound
                Toast.makeText(a, "Break Time", Toast.LENGTH_SHORT).show();
                MediaPlayer alarm = MediaPlayer.create(a, R.raw.summitalarm);
                alarm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                alarm.start();
            }
        }.start();

    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    // Resume the timer from where it was paused
    private void resumeTimer() {
        startTime(timeLeftInMillis);
    }

    // Reset the timer to the initial state
    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        countdownTimer.setText("00:00:00");
        timeLeftInMillis = 0;
    }

    // Format and update the countdown timer text
    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTimer.setText(timeFormatted);
    }
}