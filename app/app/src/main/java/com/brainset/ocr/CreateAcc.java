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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class CreateAcc extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        FbData db = new FbData();
        PasswordManager pass = new PasswordManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);
        EditText usrName = findViewById(R.id.editTextUsr);
        EditText usrPass = findViewById(R.id.editTextPass);
        EditText usrPassConfirm = findViewById(R.id.editTextConfirm);

        Button createAcc = findViewById(R.id.buttonCreate);
        Button back = findViewById(R.id.buttonBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAcc.this, Login.class);
                startActivity(intent);
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = usrName.getText().toString();
                String userPass = usrPass.getText().toString();
                String userPassConfirm = usrPassConfirm.getText().toString();

                db.getUser(userName, new FbData.UserDataListener() {
                    @Override
                    public void onUserDataRetrieved(Users user) throws IOException {
                        if (pass.isValidPass(userPass)){
                            if (userPass.equals(userPassConfirm)){
                                if (user == null){
                                    //create empty values to populate spot in database
                                    HashMap<String, Scans> emptyScans = new HashMap<>();
                                    File empty = new File(getFilesDir(), "temp.txt");
                                    FileWriter fw = new FileWriter(empty);
                                    fw.write("empty");
                                    fw.close();
                                    Scans emptyScan = new Scans("empty", empty);
                                    emptyScans.put("empty", emptyScan);
                                    String hashedPass = pass.hashPass(userPass);
                                    Users newUser = new Users(userName, hashedPass, 0, emptyScans);
                                    db.addNewUser(newUser);
                                    db.setUserScans(newUser, emptyScans);
                                    Intent intent = new Intent(CreateAcc.this, Login.class);
                                    startActivity(intent);
                                } else {
                                    Log.e("AccCreate", "Account with username (" + userName + ") already exists");
                                }
                            } else {
                                Log.e("AccCreate", "Passwords must match");
                            }
                        } else {
                            Log.e("AccCreate", "Password must contain letter and digit and contain at least 8 chars");
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
