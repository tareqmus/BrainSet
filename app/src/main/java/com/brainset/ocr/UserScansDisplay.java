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

// Class definition for UserScansDisplay, which extends Fragment to show a list of user scans
public class UserScansDisplay extends Fragment {
    View view;             // View object to hold the inflated layout for this fragment
    ListView listView;     // ListView to display the list of scans
    Activity activity;     // Reference to the activity containing this fragment

    // onCreateView is called to have the fragment instantiate its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment using the user_scan_list layout
        view = inflater.inflate(R.layout.user_scan_list, container, false);

        // Check the focus mode settings for the current activity
        FocusMode.checkFocusMode(activity);

        // Initialize the listView by finding it in the inflated layout
        listView = view.findViewById(R.id.listView);

        // Retrieve the user's scans from a global data store and convert to an array
        Object[] userScans = GlobalData.user.scans.values().toArray();
        ArrayList<Scans> scanList = new ArrayList<>();

        // Loop through the userScans array, casting each object to a Scans object and adding it to the scanList
        for (int i = 0; i < userScans.length; i++){
            Scans s = (Scans) userScans[i];
            scanList.add(s);
        }

        // Create an adapter for the listView using the custom ScanAdapter class, passing the context, layout for each row, and the list of scans
        ScanAdapter scanAdapter = new ScanAdapter(this.getContext(), R.layout.scan_row, scanList);

        // Set the adapter to the listView to display the items
        listView.setAdapter(scanAdapter);

        // Return the view for this fragment
        return view;
    }
}
