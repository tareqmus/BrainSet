package com.brainset.ocr.dao;

import android.net.Uri;
import android.util.Log;

import com.brainset.ocr.FbData;
import com.brainset.ocr.GlobalData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Scans {
    public static GlobalData gd = new GlobalData();

    public String name;

    public String imageLink;
    public String text;
    @Exclude
    private File image;

    @Exclude
    public File getImage(){
        return image;
    }

    public void setImage(File image){

    }
    public void save(String scanName){
        FbData db = new FbData();
        Log.e("SAVEIMAGE", gd.user.scans.get(scanName).getImage().toString() + " ");
        Uri imageUri = Uri.fromFile(image);
        StorageReference imageRef = db.imageFilesRef.child(imageLink + ".jpg");
        UploadTask aTask, iTask;
        gd.user.scans.put(this.name, this);
        iTask = imageRef.putFile(imageUri);
        Scans curr = this;
        iTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    curr.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void load() throws IOException {
        FbData db = new FbData();
        StorageReference imageRef = db.imageFilesRef.child(this.imageLink + ".jpg");
        this.image = File.createTempFile("my_image", "jpg");
        imageRef.getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("Load", "Loadededdd");
                Log.e("Load", image.getName());
            }
        });
    }
    public Scans(){

    }
    public Scans(String name, File image){
        this.name = name;
        this.image = image;
        Log.e("ScanMade", "scanMade " + this.image);
        this.imageLink = UUID.randomUUID().toString();
    }
    public Scans(String name){
        this.name = name;
    }
}
