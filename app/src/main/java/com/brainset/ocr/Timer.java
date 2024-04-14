package com.brainset.ocr;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer extends Fragment {
    //Activity a = this;
    TextView countdownTimer;
    EditText inputTime;
    CountDownTimer timer;
    Button start, pause, reset, resume, startFocusTime;
    long timeLeftInMillis;
    boolean focusTimeEnabled = false;
    View view;
    Activity a;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.study_timer, container, false);
        FocusMode.checkFocusMode(a);

        countdownTimer = view.findViewById(R.id.countdown_timer);
        inputTime = view.findViewById(R.id.editText); // EditText for user to input time
        start = view.findViewById(R.id.start);
        pause = view.findViewById(R.id.pause);
        reset = view.findViewById(R.id.reset);
        resume = view.findViewById(R.id.resume);
        startFocusTime = view.findViewById(R.id.focusTimeBtn);
        a = this.getActivity();

        // Add this onFocusChangeListener
        inputTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Clear the hint when EditText gains focus
                    inputTime.setText(""); // Clear any text entered by the user
                    inputTime.setHint("How long do you want to study?"); // Reset the hint to its original text
                }
            }
        });
        startFocusTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startFocusTime.getText().toString().equals("FocusTimeDisabled")){
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
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel(); // Cancel any existing timer
                }
                String inputString = inputTime.getText().toString();
                // Extract numbers and units from the input string
                Pattern pattern = Pattern.compile("(\\d+)\\s*(sec|second|seconds|min|minute|minutes|hr|hour|hours)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputString);
                if (matcher.find()) {
                    long inputValue = Long.parseLong(matcher.group(1)); // Extracted number
                    String unit = matcher.group(2).toLowerCase(); // Extracted unit
                    switch (unit) {
                        case "sec":
                        case "second":
                        case "seconds":
                            timeLeftInMillis = inputValue * 1000; // Convert seconds to milliseconds
                            break;
                        case "min":
                        case "minute":
                        case "minutes":
                            timeLeftInMillis = inputValue * 60000; // Convert minutes to milliseconds
                            break;
                        case "hr":
                        case "hour":
                        case "hours":
                            timeLeftInMillis = inputValue * 3600000; // Convert hours to milliseconds
                            break;
                    }
                    startTime(timeLeftInMillis);
                } else {

                    Toast.makeText(a.getApplicationContext(), "Please enter a valid time format (e.g., 10 seconds, 2 minutes, 1 hour).", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        // Set onClickListener for the resume button
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

        // Show recommendation popup
        //showRecommendationPopup();
        return view;
    }

    private void showRecommendationPopup() {
        new AlertDialog.Builder(a.getApplicationContext())
                .setTitle("Study Recommendation")
                .setMessage("Experts recommend studying for 25-30 minutes per session!")
                // OK button to dismiss the dialog
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void resumeTimer() {
        Log.d("TimerApp", "Resuming timer with time left: " + timeLeftInMillis + "ms");
        startTime(timeLeftInMillis);
    }
    private int focusTime = 0;
    private void startTime(long time) {
        Log.d("TimerApp", "Starting timer with time: " + time + "ms");
        if (focusTimeEnabled){
            GlobalData.user.inFocusMode = true;
            FocusMode.checkFocusMode(a);
            focusTime = 1;
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if (focusTime == 1){
                    FocusMode.exitFocus(a);
                    focusTime = 0;
                }
                Log.d("TimerApp", "Timer finished");
                countdownTimer.setText("00:00:00");

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
            Log.d("TimerApp", "Pausing timer");
            timer.cancel();
        } else {
            Log.d("TimerApp", "Pause called but timer is null");
        }
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        countdownTimer.setText("00:00:00");
        timeLeftInMillis = 0; // Reset the time

        // Clear the EditText input and optionally reset the hint
        inputTime.setText(""); // Clear any text entered by the user
        inputTime.setHint("How long do you want to study?"); // Reset the hint to its original text
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTimer.setText(timeFormatted);
    }
}