package com.adriim1.esports_calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int currentYear, currentMonth, currentDay;
    private int index = 0;
    private List<String> calendarStrings;
    private int[] days;
    private int[] months;
    private int[] years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CalendarView calendarView = findViewById(R.id.calendarView);
        final View dayContent = findViewById(R.id.dayContent);
        final EditText textInput = findViewById(R.id.textInput);
        final Button saveTextButton = findViewById(R.id.saveTextButton);
        final Button todayButton = findViewById(R.id.todayButton);

        int numDays = 2000;
        calendarStrings = new ArrayList<>();
        days = new int[numDays];
        months = new int[numDays];
        years = new int[numDays];

        readInfo();

        calendarView.setOnDateChangeListener((@NonNull CalendarView view, int year, int month, int dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            currentDay = dayOfMonth;

            if (dayContent.getVisibility() == View.GONE) {
                dayContent.setVisibility(View.VISIBLE);
            }

            boolean found = false;
            for (int i = 0; i < index; i++) {
                if (days[i] == currentDay && months[i] == currentMonth && years[i] == currentYear) {
                    textInput.setText(calendarStrings.get(i));
                    found = true;
                    break;
                }
            }

            if (!found) {
                textInput.setText("");
            }
        });

        saveTextButton.setOnClickListener(v -> {
            if (index < days.length) {
                days[index] = currentDay;
                months[index] = currentMonth;
                years[index] = currentYear;
                calendarStrings.add(index, textInput.getText().toString());
                index++;
                textInput.setText("");
                dayContent.setVisibility(View.GONE);
            }
        });

        todayButton.setOnClickListener(v -> calendarView.setDate(System.currentTimeMillis()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveInfo();
    }

    private void saveInfo() {
        try {
            File file = new File(getFilesDir(), "saved");
            File daysFile = new File(getFilesDir(), "days");
            File monthsFile = new File(getFilesDir(), "months");
            File yearsFile = new File(getFilesDir(), "years");

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                 BufferedWriter bwDays = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(daysFile)));
                 BufferedWriter bwMonths = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(monthsFile)));
                 BufferedWriter bwYears = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(yearsFile)))) {

                for (int i = 0; i < index; i++) {
                    bw.write(calendarStrings.get(i));
                    bw.newLine();
                    bwDays.write(days[i]);
                    bwMonths.write(months[i]);
                    bwYears.write(years[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving info", e);
        }
    }

    private void readInfo() {
        File file = new File(getFilesDir(), "saved");
        File daysFile = new File(getFilesDir(), "days");
        File monthsFile = new File(getFilesDir(), "months");
        File yearsFile = new File(getFilesDir(), "years");

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
             BufferedReader readerDays = new BufferedReader(new InputStreamReader(new FileInputStream(daysFile)));
             BufferedReader readerMonths = new BufferedReader(new InputStreamReader(new FileInputStream(monthsFile)));
             BufferedReader readerYears = new BufferedReader(new InputStreamReader(new FileInputStream(yearsFile)))) {

            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < days.length) {
                calendarStrings.add(line);
                days[i] = readerDays.read();
                months[i] = readerMonths.read();
                years[i] = readerYears.read();
                i++;
            }
            index = i;
        } catch (Exception e) {
            Log.e(TAG, "Error reading info", e);
        }
    }
}
