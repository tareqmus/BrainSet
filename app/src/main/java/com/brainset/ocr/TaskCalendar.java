package com.brainset.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

// Defines the Calendar class extending AppCompatActivity to use Android's Activity features
public class TaskCalendar extends AppCompatActivity {
    // Class variables for storing current selected date and indexing for saved dates
    private int currentYear = 0;
    private int currentMonth = 0;
    private int currentDay = 0;

    private int index = 0; // Used to keep track of the number of entries

    // Lists and arrays to store calendar entries and their corresponding dates
    private List<String> calendarStrings;
    private int[] days;
    private int[] months;
    private int[] years;

    // Lifecycle method called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FocusMode.checkFocusMode(this);
        setContentView(R.layout.activity_calendar); // Sets the layout for the activity

        // Initializing UI components
        final CalendarView calendarView = findViewById(R.id.calendarView);
        calendarStrings = new ArrayList<>();
        final int numberOfDays = 2000; // Predefined size for arrays to store date components
        days = new int[numberOfDays];
        months = new int[numberOfDays];
        years = new int[numberOfDays];

        readInfo(); // Read saved calendar info from storage

        final EditText textInput = findViewById(R.id.textInput);
        final View dayContent = findViewById(R.id.dayContent);

        // Listener for date selection on the calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentYear = year;
                currentMonth = month;
                currentDay = dayOfMonth;

                // Display the content area when a day is selected
                if (dayContent.getVisibility() == View.GONE) {
                    dayContent.setVisibility(View.VISIBLE);
                }

                // Search through saved dates to find a match and display the saved text
                for (int h = 0; h < index; h++) {
                    if (years[h] == currentYear) {
                        for (int i = 0; i < index; i++) {
                            if (days[i] == currentDay) {
                                for (int j = 0; j < index; j++) {
                                    if (months[j] == currentMonth && days[j] == currentDay && years[j] == currentYear) {
                                        textInput.setText(calendarStrings.get(j));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                textInput.setText(" "); // Clear the text input if no entry is found
            }
        });

        // Button to save the text for the selected date
        final Button saveTextBTN = findViewById(R.id.saveTextbtn);

        // Listener for the save button
        saveTextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean entryExists = false;
                for (int i = 0; i < index; i++) {
                    if (days[i] == currentDay && months[i] == currentMonth && years[i] == currentYear) {
                        // Entry for this date exists, update it
                        calendarStrings.set(i, textInput.getText().toString());
                        entryExists = true;
                        break; // Exit the loop once the entry is found and updated
                    }
                }
                if (!entryExists) {
                    // No entry exists for this date, add a new one
                    if (index < days.length) { // Ensure there's room to add a new entry
                        days[index] = currentDay;
                        months[index] = currentMonth;
                        years[index] = currentYear;
                        calendarStrings.add(index, textInput.getText().toString());
                        index++; // Increment index for the next entry
                    } else {
                        // Handle the case where the arrays are full
                        // You could expand the arrays or inform the user
                    }
                }

                textInput.setText(" "); // Clear the text input after saving
                dayContent.setVisibility(View.GONE); // Optionally hide the content area
            }
        });

        // Button to reset the calendar view to today's date
        final Button todayButton = findViewById(R.id.todaybtn);

        // Listener for the "Today" button
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets the calendar to the current date
                calendarView.setDate(calendarView.getDate());
            }
        });
    }

    // Lifecycle method called when the activity goes into the background
    @Override
    protected void onPause() {
        super.onPause();
        saveInfo(); // Saves the calendar info to storage
    }

    // Method to save calendar entries to internal storage
    private void saveInfo() {
        // Files for storing calendar data
        File file = new File(this.getFilesDir(), "saved");
        File daysFile = new File(this.getFilesDir(), "days");
        File monthsFile = new File(this.getFilesDir(), "months");
        File yearsFile = new File(this.getFilesDir(), "years");

        try {
            // Setup writers for each file
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            FileOutputStream fOutDays = new FileOutputStream(daysFile);
            BufferedWriter bwDays = new BufferedWriter(new OutputStreamWriter(fOutDays));

            FileOutputStream fOutMonths = new FileOutputStream(monthsFile);
            BufferedWriter bwMonths = new BufferedWriter(new OutputStreamWriter(fOutMonths));

            FileOutputStream fOutYears = new FileOutputStream(yearsFile);
            BufferedWriter bwYears = new BufferedWriter(new OutputStreamWriter(fOutYears));

            // Write each entry to its respective file
            for (int i = 0; i < index; i++) {
                bw.write(calendarStrings.get(i));
                bw.newLine();
                bwDays.write(days[i]);
                bwMonths.write(months[i]);
                bwYears.write(years[i]);
            }

            // Close all writers
            bw.close();
            fOut.close();
            bwDays.close();
            fOutDays.close();
            bwMonths.close();
            fOutMonths.close();
            bwYears.close();
            fOutYears.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to read saved calendar entries from internal storage
    private void readInfo() {
        // Files where the calendar data is stored
        File file = new File(this.getFilesDir(), "saved");
        File daysFile = new File(this.getFilesDir(), "days");
        File monthsFile = new File(this.getFilesDir(), "months");
        File yearsFile = new File(this.getFilesDir(), "years");

        if (!file.exists()) {
            return; // Exit if the main file doesn't exist
        }
        try {
            // Setup readers for each file
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            FileInputStream isDays = new FileInputStream(daysFile);
            BufferedReader readerDays = new BufferedReader(new InputStreamReader(isDays));
            FileInputStream isMonths = new FileInputStream(monthsFile);
            BufferedReader readerMonths = new BufferedReader(new InputStreamReader(isMonths));
            FileInputStream isYears = new FileInputStream(yearsFile);
            BufferedReader readerYears = new BufferedReader(new InputStreamReader(isYears));

            int i = 0;
            String line = reader.readLine();

            // Read each line and populate the arrays and list with saved data
            while (line != null) {
                calendarStrings.add(line);
                line = reader.readLine();
                days[i] = readerDays.read();
                months[i] = readerMonths.read();
                years[i] = readerYears.read();
                i++;
            }

            index = i; // Update the index with the number of entries read
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}