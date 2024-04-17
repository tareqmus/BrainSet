package com.brainset.ocr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.brainset.ocr.HomeFragment;
import com.brainset.ocr.R;
import com.brainset.ocr.TaskCalendar;
import com.brainset.ocr.Timer;
import com.brainset.ocr.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

public class Dashboard extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    ActivityDashboardBinding binding;
    public static Fragment home, calendar, timer, settings, userScans;

    private void initFrags(){
        home = new UserScansDisplay();
        calendar = new TaskCalendar();
        timer = new Timer();
        settings = new ttsModify();
        userScans = new UserScansDisplay();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        initFrags();
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new UserScansDisplay());
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeId){
                replaceFragment(userScans);
            } else if (item.getItemId() == R.id.calendarId){
                replaceFragment(calendar);
            } else if (item.getItemId() == R.id.clockId){
                replaceFragment(timer);
            } else if (item.getItemId() == R.id.settingsId) {
                replaceFragment(settings);
            }
            return true;
        });

            // Add Button
        FloatingActionButton startButton = findViewById(R.id.fab);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click action here
                Intent intent = new Intent(Dashboard.this, Gallery.class);
                startActivity(intent);
            }
        });

        // Example of accessing BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);



        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        }
    }

    public void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, f);
        ft.commit();
    }

}

