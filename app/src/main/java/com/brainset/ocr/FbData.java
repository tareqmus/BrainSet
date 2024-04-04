package com.brainset.ocr;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brainset.ocr.dao.Scans;
import com.brainset.ocr.dao.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FbData {

    public static DatabaseReference usersRef, scansRef;
    public StorageReference audioFilesRef, imageFilesRef;
    GlobalData gd = new GlobalData();
    public FbData(){
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        scansRef = FirebaseDatabase.getInstance().getReference("scans");
        audioFilesRef = FirebaseStorage.getInstance().getReference("text/");
        imageFilesRef = FirebaseStorage.getInstance().getReference("images/");
        String key = scansRef.push().getKey();
        Log.e("KEY", key);
    }
    /*Uri imageUri = Uri.fromFile(image);
    Uri audioUri = Uri.fromFile(audio);
    StorageReference imageRef = imageFilesRef.child(UUID.randomUUID().toString() + ".jpg");
    StorageReference audioRef = audioFilesRef.child(UUID.randomUUID().toString() + ".mp3");
    UploadTask aTask, iTask;
    aTask = audioRef.putFile(audioUri);
    iTask = imageRef.putFile(imageUri);*/


    public interface UserDataListener {
        void onUserDataRetrieved(Users user) throws IOException;
        void onError(DatabaseError error);
    }
    public void addNewUser(Users user){
        Log.e("", usersRef.toString());
        Log.e("", user.imei);
        //usersRef.child("hi").setValue(user);
        usersRef.child(user.imei).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("added", "Added user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("added", "Fail");
            }
        });
    }

    public void setUserScans(Users user, HashMap<String, Scans> scans){
        usersRef.child(user.imei).child("scans").setValue(scans);
    }
    public void getUser(String imei, UserDataListener listener){
        Task<DataSnapshot> t = usersRef.child(imei).get();
        t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                gd.user = task.getResult().getValue(Users.class);
                try {
                    listener.onUserDataRetrieved(gd.user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        /*usersRef.child(imei).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gd.user = snapshot.getValue(Users.class);
                Log.e("data", "dataChanged");
                try {
                    listener.onUserDataRetrieved(gd.user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        /*Task<DataSnapshot> t = usersRef.child(imei).get();
        t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.e("", task.toString());
                Users user = t.getResult().getValue(Users.class);
                listener.onUserDataRetrieved(user);
            }
        });*/
    }
}
