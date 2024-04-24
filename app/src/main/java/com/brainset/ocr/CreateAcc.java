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
import java.util.HashMap;

// CreateAcc class definition, extending AppCompatActivity for UI interaction
public class CreateAcc extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase and password management helpers
        FbData db = new FbData();
        PasswordManager pass = new PasswordManager();

        // Standard calls for activity setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);

        // UI element bindings
        EditText usrName = findViewById(R.id.editTextUsr);
        EditText usrPass = findViewById(R.id.editTextPass);
        EditText usrPassConfirm = findViewById(R.id.editTextConfirm);

        // Buttons for creating account and navigating back
        Button createAcc = findViewById(R.id.buttonCreate);
        Button back = findViewById(R.id.buttonBack);

        // Listener for the Back button to navigate back to the Login screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAcc.this, Login.class);
                startActivity(intent);
            }
        });

        // Listener for the Create Account button
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch user input
                String userName = usrName.getText().toString();
                String userPass = usrPass.getText().toString();
                String userPassConfirm = usrPassConfirm.getText().toString();

                // Check if user exists in the database
                db.getUser(userName, new FbData.UserDataListener() {
                    @Override
                    public void onUserDataRetrieved(Users user) throws IOException {
                        // Validate password according to predefined rules
                        if (pass.isValidPass(userPass)) {
                            if (userPass.equals(userPassConfirm)) {
                                // Check if user does not already exist
                                if (user == null) {
                                    // Prepare placeholder data for new user
                                    HashMap<String, Scans> emptyScans = new HashMap<>();
                                    File empty = new File(getFilesDir(), "temp.txt");
                                    FileWriter fw = new FileWriter(empty);
                                    fw.write("empty");
                                    fw.close();

                                    // Create an empty scan entry for the new user
                                    Scans emptyScan = new Scans("empty", empty);
                                    emptyScans.put("empty", emptyScan);

                                    // Hash the password and create new user object
                                    String hashedPass = pass.hashPass(userPass);
                                    Users newUser = new Users(userName, hashedPass, 0, emptyScans);

                                    // Add new user to database and initialize their scans
                                    db.addNewUser(newUser);
                                    db.setUserScans(newUser, emptyScans);

                                    // Redirect user to login after account creation
                                    Intent intent = new Intent(CreateAcc.this, Login.class);
                                    startActivity(intent);
                                } else {
                                    // Log error if user already exists
                                    Log.e("AccCreate", "Account with username (" + userName + ") already exists");
                                }
                            } else {
                                // Log error if passwords do not match
                                Log.e("AccCreate", "Passwords must match");
                            }
                        } else {
                            // Log error if password does not meet criteria
                            Log.e("AccCreate", "Password must contain letter and digit and contain at least 8 chars");
                        }
                    }

                    @Override
                    public void onError(DatabaseError error) {
                        // Handle potential errors during the database operations
                    }
                });
            }
        });
    }
}