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

import com.brainset.ocr.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class Dashboard extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    ActivityDashboardBinding binding;
    public static Fragment home, calendar, timer, settings;

    private void initFrags(){
        home = new HomeFragment();
        calendar = new TaskCalendar();
        timer = new Timer();
        settings = new HomeFragment();
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
        replaceFragment(new HomeFragment());
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                //home
                case 2131296507:
                    Log.e("here", "home selected");
                    replaceFragment(home);
                    break;
                //calendar
                case 2131296371:
                    Log.e("here", "Cal selected");
                    replaceFragment(calendar);
                    break;
                //timer
                case 2131296394:
                    Log.e("here", "Clock selected");
                    replaceFragment(timer);
                    break;
                //settings
                case 2131296704:
                    Log.e("here", "Settings selected");
                    replaceFragment(settings);
                    break;

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