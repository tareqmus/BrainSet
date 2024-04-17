package com.brainset.ocr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.brainset.ocr.dao.Scans;
import com.brainset.ocr.dao.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

public class FbData {

    public static DatabaseReference usersRef, scansRef, tipRef;
    public StorageReference audioFilesRef, imageFilesRef;
    GlobalData gd = new GlobalData();
    public FbData(){
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        scansRef = FirebaseDatabase.getInstance().getReference("scans");
        tipRef = FirebaseDatabase.getInstance().getReference("tips");
        audioFilesRef = FirebaseStorage.getInstance().getReference("text/");
        imageFilesRef = FirebaseStorage.getInstance().getReference("images/");
        String key = scansRef.push().getKey();
        Log.e("KEY", key);
    }

    public interface ScanDataListener {
        void onScansRetrieved(HashMap<String, Scans> scans) throws IOException;
    }
    public interface UserDataListener {
        void onUserDataRetrieved(Users user) throws IOException;
        void onError(DatabaseError error);
    }
    public void addNewUser(Users user){
        usersRef.child(user.userName).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        usersRef.child(user.userName).child("scans").setValue(scans);
    }

    public void getUserScans(Users user, ScanDataListener listener){
        Task<DataSnapshot> t = usersRef.child(user.userName).child("scans").get();
        t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                GenericTypeIndicator<HashMap<String, Scans>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Scans>>() {};
                gd.user.scans = task.getResult().getValue(genericTypeIndicator);
                //Log.e("CREATE DASH", GlobalData.user.scans.get("ScanTest").name);
                try {
                    listener.onScansRetrieved(gd.user.scans);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void getUser(String userName, UserDataListener listener){
        Task<DataSnapshot> t = usersRef.child(userName).get();
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

    }
}
