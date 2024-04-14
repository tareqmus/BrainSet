package com.brainset.ocr.dao;

import static com.brainset.ocr.dao.Scans.gd;

import android.util.Log;

import com.brainset.ocr.Gallery;
import com.google.firebase.database.Exclude;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Users {
    public String androidId;
    public String userName;
    public String passwordHash;

    public int rewardsPoints;
    public static HashMap<String, Scans> scans;
    @Exclude
    public boolean inFocusMode = false;


    public Users() {

    }

    public Users(String usrName, String passHash, int points, HashMap<String, Scans> scans){
        this.userName = usrName;
        this.passwordHash = passHash;
        this.rewardsPoints = points;
        this.scans = scans;
    }

    public Users(String androidId, int points, HashMap<String, Scans> scans){
        this.androidId = androidId;
        this.rewardsPoints = points;
        this.scans = scans;
    }

    public Users(String androidId, int points){
        this.androidId = androidId;
        this.rewardsPoints = points;
        this.scans = new HashMap<>();

    }
    public interface LoadCompleteListener{
        public void onLoadComplete();
    }
    public static void saveScans(){
        for(Map.Entry<String, Scans> entry : scans.entrySet()){
            //Log.e("Saving", "Saving " + entry.getValue().imageLink);
            if (!entry.getValue().equals(new Scans("empty", gd.user.scans.get("empty").getImage()))){
                Log.e("Saving", "Saving " + entry.getValue().imageLink);
                //entry.getValue().save();
            }

        }
    }
    public void loadScans() throws IOException {
        Log.e("loading", scans.get("name").name);
        for(Map.Entry<String, Scans> entry : scans.entrySet()){
            try {
                Log.e("getting", entry.getValue().name);
                entry.getValue().load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //listener.onLoadComplete();
    }
    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public int getRewardsPoints() {
        return rewardsPoints;
    }

    public void setRewardsPoints(int rewardsPoints) {
        this.rewardsPoints = rewardsPoints;
    }
}
