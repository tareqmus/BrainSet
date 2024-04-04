package com.brainset.ocr;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.brainset.ocr.dao.Scans;
import com.brainset.ocr.dao.Users;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class LoadingScreen extends AppCompatActivity {


    String imei = "didnt work";
    GlobalData gd = new GlobalData();
    HashMap<String, Scans> emptyScans = new HashMap<>();
    Scans emptyScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FbData db = new FbData();
        db.addNewUser(new Users(" ", 123));
        emptyScan = new Scans("empty", new File(getFilesDir(), "my_text_file.txt"));
        try {
            FileWriter fw = new FileWriter(emptyScan.getImage());
            fw.write("1234");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emptyScans.put("empty", emptyScan);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);


        //get imei
        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("imei", mId);


        //create dummy user for new users
        Users user1 = new Users(mId, 0, emptyScans);

        //getUser if it exists in db, if not add dummy to db
        db.getUser(mId, new FbData.UserDataListener() {
            @Override
            public void onUserDataRetrieved(Users user) throws IOException {
                if (user != null){
                    gd.user = user;
                    Log.e("LOAD", gd.user.androidId + "USER ALREADY IN DB");
                } else {
                    gd.user = user1;
                    db.addNewUser(gd.user);
                    gd.user.scans.clear();
                    Log.e("LOAD", gd.user.androidId + "USER NOT IN DB");
                }
                Intent intent = new Intent(LoadingScreen.this, Gallery.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onError(DatabaseError error) {

            }
        });

    }

}