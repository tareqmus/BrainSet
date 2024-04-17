package com.brainset.ocr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.brainset.ocr.dao.Scans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class UserScansDisplay extends Fragment {
    View view;
    ListView listView;
    Activity activity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.user_scan_list, container, false);
        FocusMode.checkFocusMode(activity);


        listView = view.findViewById(R.id.listView);
        Object[] userScans = GlobalData.user.scans.values().toArray();
        ArrayList<Scans> scanList = new ArrayList<>();
        for (int i = 0; i < userScans.length; i++){
            Scans s = (Scans) userScans[i];
            scanList.add(s);
        }
        ScanAdapter scanAdapter = new ScanAdapter(this.getContext(), R.layout.scan_row, scanList);
        listView.setAdapter(scanAdapter);



        return view;
    }







}
