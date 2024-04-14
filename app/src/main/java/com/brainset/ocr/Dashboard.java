package com.brainset.ocr;

import static android.text.TextUtils.replace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;

import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Dashboard extends AppCompatActivity {

    protected final int home = 1;
    protected final int calendar = 2;
    protected final int pluscan = 3;
    protected final int timer = 4;
    protected final int settings = 5;

    // Declare the replace method outside any method
    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }


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


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meowBottomNavigation);
        RelativeLayout main_layout;
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(calendar, R.drawable.baseline_calendar));
        bottomNavigation.add(new MeowBottomNavigation.Model(pluscan, R.drawable.baseline_add));
        bottomNavigation.add(new MeowBottomNavigation.Model(timer, R.drawable.baseline_clock));
        bottomNavigation.add(new MeowBottomNavigation.Model(settings, R.drawable.baseline_settings));

        // Set the "Home" button as selected by default
        bottomNavigation.show(home, true);
        replace(new HomeFragment());

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {

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
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case home:
                        replace(new HomeFragment());
                        break;
                    case pluscan:
                        // Navigate to Gallery activity
                        Intent galleryIntent = new Intent(Dashboard.this, Gallery.class);
                        startActivity(galleryIntent);
                        break;
                    case calendar:
                        // Navigate to TaskCalendar activity
                        replace(new CalendarFragment());
                        break;
                    case timer:
                        // Navigate to Timer activity
                        Intent timerIntent = new Intent(Dashboard.this, Timer.class);
                        startActivity(timerIntent);
                        break;
                }
                return null;
            }
        });



        /*
        // Add Button
        FloatingActionButton startButton = findViewById(R.id.fab);
        startButton.setOnClickListener(v -> showBottomDialog());

        // Example of accessing BottomNavigationView



        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        }

         */
    }

    private void showBottomDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetdash, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        LinearLayout galleryLayout = view.findViewById(R.id.bottomsheetGallery);
        LinearLayout cameraLayout = view.findViewById(R.id.bottomsheetCamera);

        galleryLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(Dashboard.this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show();
        });

        cameraLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(Dashboard.this, "Create a short is Clicked", Toast.LENGTH_SHORT).show();
        });
    }

}


    public void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, f);
        ft.commit();
    }
}

