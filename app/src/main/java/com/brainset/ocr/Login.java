package com.brainset.ocr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.brainset.ocr.dao.Scans;
import com.brainset.ocr.dao.Users;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    GlobalData gd = new GlobalData();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        FbData db = new FbData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText usrName = findViewById(R.id.editTextText);
        EditText usrPass = findViewById(R.id.editTextText2);
        Button loginB = findViewById(R.id.button7);
        Button createAccB = findViewById(R.id.buttonCreateAcc);


        createAccB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, CreateAcc.class);
                startActivity(intent);
                finish();
            }
        });

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = usrName.getText().toString();
                String userPass = usrPass.getText().toString();
                PasswordManager p = new PasswordManager();
                db.getUser(userName, new FbData.UserDataListener() {
                    @Override
                    public void onUserDataRetrieved(Users user) throws IOException {
                        if (user == null){
                            Log.e("Login", "Incorrect Username");
                            return;
                        } else {
                            gd.user = user;
                            db.getUserScans(gd.user, new FbData.ScanDataListener() {
                                @Override
                                public void onScansRetrieved(HashMap<String, Scans> scans) throws IOException {
                                    gd.user.scans = scans;
                                    gd.user.loadScans();
                                    if (gd.user.passwordHash.equals(p.hashPass(userPass))){
                                        Intent intent = new Intent(Login.this, Dashboard.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e("Login", "Incorrect Password");
                                    }
                                }
                            });



                        }

                    }

                    @Override
                    public void onError(DatabaseError error) {

                    }
                });
            }
        });




    }
}
