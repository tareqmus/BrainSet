// Package declaration
package com.brainset.ocr;

// Import statements for Android and Java utilities
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

// Definition of the CalendarFragment class which extends Fragment
public class CalendarFragment extends Fragment {

    // Instance variables for storing the currently selected date
    private int currentYear = 0;
    private int currentMonth = 0;
    private int currentDay = 0;

    // Index to keep track of number of entries
    private int index = 0;
    // List to store calendar entries as strings
    private List<String> calendarStrings;
    // Arrays to store date information for each entry
    private int[] days;
    private int[] months;
    private int[] years;

    // Method to create and return the view hierarchy associated with the fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize the CalendarView and data structures
        final CalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarStrings = new ArrayList<>();
        final int numberOfDays = 2000;  // Capacity for the arrays
        days = new int[numberOfDays];
        months = new int[numberOfDays];
        years = new int[numberOfDays];

        // Read previously saved info
        readInfo();

        // Set up user input field and visibility handler
        final EditText textInput = rootView.findViewById(R.id.textInput);
        final View dayContent = rootView.findViewById(R.id.dayContent);

        // Listener for date changes on the calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentYear = year;
                currentMonth = month;
                currentDay = dayOfMonth;

                // Show the content view when a day is selected
                if (dayContent.getVisibility() == View.GONE) {
                    dayContent.setVisibility(View.VISIBLE);
                }

                // Display the entry for the selected day, if it exists
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
                textInput.setText(" ");
            }
        });

        // Button to save or update an entry
        final Button saveTextBTN = rootView.findViewById(R.id.saveTextbtn);
        saveTextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean entryExists = false;
                for (int i = 0; i < index; i++) {
                    if (days[i] == currentDay && months[i] == currentMonth && years[i] == currentYear) {
                        calendarStrings.set(i, textInput.getText().toString());
                        entryExists = true;
                        break;
                    }
                }
                if (!entryExists) {
                    if (index < days.length) {
                        days[index] = currentDay;
                        months[index] = currentMonth;
                        years[index] = currentYear;
                        calendarStrings.add(index, textInput.getText().toString());
                        index++;
                    }
                }

                // Clear input and hide content view after saving
                textInput.setText(" ");
                dayContent.setVisibility(View.GONE);
            }
        });

        // Button to reset the calendar to the current date
        final Button todayButton = rootView.findViewById(R.id.todaybtn);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.setDate(calendarView.getDate());
            }
        });

        return rootView;
    }

    // Method to handle fragment pausing, saves current state
    @Override
    public void onPause() {
        super.onPause();
        saveInfo();
    }

    // Method to save all entries to internal storage
    private void saveInfo() {
        // File handling for storing entries
        File file = new File(requireContext().getFilesDir(), "saved");
        File daysFile = new File(requireContext().getFilesDir(), "days");
        File monthsFile = new File(requireContext().getFilesDir(), "months");
        File yearsFile = new File(requireContext().getFilesDir(), "years");

        try {
            // Output streams and writers for file writing
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            FileOutputStream fOutDays = new FileOutputStream(daysFile);
            BufferedWriter bwDays = new BufferedWriter(new OutputStreamWriter(fOutDays));

            FileOutputStream fOutMonths = new FileOutputStream(monthsFile);
            BufferedWriter bwMonths = new BufferedWriter(new OutputStreamWriter(fOutMonths));

            FileOutputStream fOutYears = new FileOutputStream(yearsFile);
            BufferedWriter bwYears = new BufferedWriter(new OutputStreamWriter(fOutYears));

            // Write data to files
            for (int i = 0; i < index; i++) {
                bw.write(calendarStrings.get(i));
                bw.newLine();
                bwDays.write(days[i]);
                bwMonths.write(months[i]);
                bwYears.write(years[i]);
            }

            // Close all streams
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

    // Method to read saved entries from internal storage
    private void readInfo() {
        // File handling for reading entries
        File file = new File(requireContext().getFilesDir(), "saved");
        File daysFile = new File(requireContext().getFilesDir(), "days");
        File monthsFile = new File(requireContext().getFilesDir(), "months");
        File yearsFile = new File(requireContext().getFilesDir(), "years");

        // Return early if files do not exist
        if (!file.exists()) {
            return;
        }
        try {
            // Input streams and readers for file reading
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            FileInputStream isDays = new FileInputStream(daysFile);
            BufferedReader readerDays = new BufferedReader(new InputStreamReader(isDays));
            FileInputStream isMonths = new FileInputStream(monthsFile);
            BufferedReader readerMonths = new BufferedReader(new InputStreamReader(isMonths));
            FileInputStream isYears = new FileInputStream(yearsFile);
            BufferedReader readerYears = new BufferedReader(new InputStreamReader(isYears));

            // Read and store data
            int i = 0;
            String line = reader.readLine();

            while (line != null) {
                calendarStrings.add(line);
                line = reader.readLine();
                days[i] = readerDays.read();
                months[i] = readerMonths.read();
                years[i] = readerYears.read();
                i++;
            }

            index = i;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
