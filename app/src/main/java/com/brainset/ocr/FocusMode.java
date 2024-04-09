package com.brainset.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FocusMode extends AppCompatActivity {

        //declaring the buttons from activity_main.xml
        Button b_enable, b_lock, b_back;
        //bool to hold whether in focus mode or not
        public static boolean inFocusMode = false;
        //two variables below are used to activate device admin
        static final int RESULT_ENABLED = 1;
        DevicePolicyManager devicePolicyManager;
        //identifier for ACTIVITY in androidmanifest.xml
        ComponentName componentName;
        private static String adminPassword;
        //method to check to see if focus mode it enabled it and adjust current activity properly
        public static void checkFocusMode(Activity a){
            if (inFocusMode == true){
                View decorView = a.getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else {

            }
        }

        // method to exit fullscreenmode
        private void exitFullScreenMode() {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            inFocusMode = false;
        }
        //method used to enable enterfullscreenmode
        public void enterFullScreenMode() {
            inFocusMode = true;
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        @Override
        //method used to stop user from exiting the app with back or home button (wip)
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(getApplicationContext(), "Back Button is blocked during FocusMode", Toast.LENGTH_LONG).show();
                return false;
            }
            return false;
        }

        @SuppressLint("SetTextI18n")
        @Override
        //the following class request the user to enable device admin
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            checkFocusMode(this);
            setContentView(R.layout.focus_mode);
            b_enable = findViewById(R.id.b_enable);
            b_lock = findViewById(R.id.b_lock);
            b_back = findViewById(R.id.b_back);
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            componentName = new ComponentName(FocusMode.this, Controller.class);
            boolean active = devicePolicyManager.isAdminActive(componentName);
            if (active) {
                b_enable.setText("Disable");
                b_lock.setVisibility(View.VISIBLE);
            } else {
                b_enable.setText("Enable");
                b_lock.setVisibility(View.GONE);
            }
            //if focus mode is enabled set button to unlock
            if (inFocusMode){
                b_lock.setText("Unlock");
            }

            //return back to gallery screen
            b_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FocusMode.this, Gallery.class);
                    startActivity(intent);
                }
            });
            //after enabling device admin, this class removes the enable button.
            b_enable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean active = devicePolicyManager.isAdminActive(componentName);
                    Log.e("ACTIVE", active + "");
                    if (active) {
                        devicePolicyManager.removeActiveAdmin(componentName);
                        b_enable.setText("Enable");
                        b_lock.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable the app");
                        startActivityForResult(intent, RESULT_ENABLED);
                    }
                }
            });
            //class used to enter/exit fullscreen
            b_lock.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e("lockBtn", "Cliked");
                    if (adminPassword != null){
                        Log.e("adminPasss", adminPassword);
                    }

                    if (b_lock.getText().equals("Lock")) {
                        // If it says "Lock", change it to "Unlock"
                        b_lock.setText("Unlock");
                        setAdminPassword();
                        enterFullScreenMode();
                    } else {
                        if(adminPassword == null || adminPassword.isEmpty()){
                            Toast.makeText(getApplicationContext(),"Parental Code not set", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        InputPassword();
                    }
                }
            });
        }
        private void setAdminPassword(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Parental PassCode");
            builder.setMessage("please input an 8 digit code");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String password = input.getText().toString();
                    if(validatePassword(password)){
                        adminPassword = password;
                        getPassword(password);
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid password",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        private void InputPassword(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Parental Code");
            builder.setMessage("please input the 8 digit code");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String password = input.getText().toString();
                    if(password.equals(adminPassword)){
                        exitFullScreenMode();
                        b_lock.setText("Lock");
                    }else{
                        Toast.makeText(getApplicationContext(),"Incorrect Passcode",Toast.LENGTH_SHORT).show();
                        InputPassword();
                    }
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        private void getPassword(String password){
            SharedPreferences sharedPref = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Admin Password", password);
            editor.apply();
        }
        private boolean validatePassword(String password){
            return password.length() == 8;
        }

        //suppressing "Lock" as it causes warning
        @SuppressLint("SetTextI18n")
        @Override
        //class checks if device admin has been enabled
        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
            if (requestCode == RESULT_ENABLED) {
                if (resultCode == Activity.RESULT_OK) {
                    b_enable.setText("LOCK");
                } else {
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

