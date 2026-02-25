package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private int currentYear, currentMonth, currentDay;
    private RecyclerView matchesRecyclerView;
    private EventoAdapter matchAdapter;
    private List<Evento> allMatches;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        View dayContent = view.findViewById(R.id.dayContent);
        Button todayButton = view.findViewById(R.id.todayButton);
        
        matchesRecyclerView = view.findViewById(R.id.recycler_view_matches);
        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        apiService = RetrofitClient.getApiService();
        allMatches = new ArrayList<>();
        matchAdapter = new EventoAdapter(new ArrayList<>());
        matchesRecyclerView.setAdapter(matchAdapter);

        loadAllMatches();

        calendarView.setOnDateChangeListener((@NonNull CalendarView cv, int year, int month, int dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            currentDay = dayOfMonth;
            loadMatchesForDate(year, month, dayOfMonth);
        });

        todayButton.setOnClickListener(v -> calendarView.setDate(System.currentTimeMillis()));

        return view;
    }

    private void loadAllMatches() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        currentYear = calendar.get(java.util.Calendar.YEAR);
        currentMonth = calendar.get(java.util.Calendar.MONTH);
        currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        
        loadMatchesForDate(currentYear, currentMonth, currentDay);
    }

    private void loadMatchesForDate(int year, int month, int day) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();
        
        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        Log.d(TAG, "Cargando partidos para fecha: " + date);
        
        apiServiceConToken.getEventsByDate(date).enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Evento> dayMatches = response.body().getData();
                    Log.d(TAG, "Partidos del día cargados: " + (dayMatches != null ? dayMatches.size() : "null"));
                    
                    // Actualizar el adapter directamente con los partidos del día
                    if (matchAdapter != null && matchAdapter.getEventoList() != null) {
                        matchAdapter.getEventoList().clear();
                        if (dayMatches != null) {
                            matchAdapter.getEventoList().addAll(dayMatches);
                        }
                        matchAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "matchAdapter o getEventoList() es null");
                    }
                } else {
                    Log.e(TAG, "Error en respuesta");
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexion cargando matches para fecha: " + date, t);
            }
        });
    }
}
