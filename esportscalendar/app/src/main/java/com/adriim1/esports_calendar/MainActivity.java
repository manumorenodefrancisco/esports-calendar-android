package com.adriim1.esports_calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int currentYear, currentMonth, currentDay;
    
    // Variables para matches
    private RecyclerView matchesRecyclerView;
    private MatchAdapter matchAdapter;
    private List<Match> allMatches;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarView = findViewById(R.id.calendarView);
        View dayContent = findViewById(R.id.dayContent);
        Button todayButton = findViewById(R.id.todayButton);
        ImageView navNotifications = findViewById(R.id.nav_notifications);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        matchesRecyclerView = findViewById(R.id.recycler_view_matches);
        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        apiService = RetrofitClient.getApiService();
        allMatches = new ArrayList<>();

        loadAllMatches();

        calendarView.setOnDateChangeListener((@NonNull CalendarView view, int year, int month, int dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            currentDay = dayOfMonth;

            showMatchesForDay(year, month, dayOfMonth);
        });

        todayButton.setOnClickListener(v -> calendarView.setDate(System.currentTimeMillis()));

        // navegacion
        /*navHome.setOnClickListener(v -> {
        });*/

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
    }

    private void loadAllMatches() {
        SharedPreferences prefs = getSharedPreferences("EsportsCalendarPrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();
        
        apiServiceConToken.getEvents().enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allMatches = response.body().getData();
                    Log.d(TAG, "Cargados " + allMatches.size() + " matches");
                } else {
                    Log.e(TAG, "Error cargando matches - Response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexion cargando matches", t);
            }
        });
    }

    private void showMatchesForDay(int year, int month, int day) {
        List<Match> dayMatches = new ArrayList<>();
        
        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        Log.d(TAG, "Fecha seleccionada: " + selectedDate);
        
        for (Match match : allMatches) {
            if (match.getScheduled_at() != null && match.getScheduled_at().length() >= 10) {
                String matchDate = match.getScheduled_at().substring(0, 10); // "2026-02-23"
                
                if (matchDate.equals(selectedDate)) {
                    dayMatches.add(match);
                    Log.d(TAG, "Match anadido: " + match.getVideogame_name());
                }
            }
        }
        
        Log.d(TAG, "Total matches para el dia: " + dayMatches.size());
        
        matchAdapter = new MatchAdapter(dayMatches);
        matchesRecyclerView.setAdapter(matchAdapter);
        
        Log.d(TAG, "RecyclerView actualizado con " + dayMatches.size() + " items");
    }
}
