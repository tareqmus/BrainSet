package com.brainset.ocr.dao;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Users {
    public String imei;

    public int rewardsPoints;
    public HashMap<String, Scans> scans;


    public Users() {

    }

    public Users(String imei, int points, HashMap<String, Scans> scans){
        this.imei = imei;
        this.rewardsPoints = points;
        this.scans = scans;
    }

    public Users(String imei, int points){
        this.imei = imei;
        this.rewardsPoints = points;
        this.scans = new HashMap<>();

    }
    public interface LoadCompleteListener{
        public void onLoadComplete();
    }
    public void saveScans(){
        for(Map.Entry<String, Scans> entry : scans.entrySet()){
            entry.getValue().save();
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
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getRewardsPoints() {
        return rewardsPoints;
    }

    public void setRewardsPoints(int rewardsPoints) {
        this.rewardsPoints = rewardsPoints;
    }
}
