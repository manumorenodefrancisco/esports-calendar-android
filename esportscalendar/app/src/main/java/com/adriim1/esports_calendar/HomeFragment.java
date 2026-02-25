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
    private MatchAdapter matchAdapter;
    private List<Match> allMatches;
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

        loadAllMatches();

        calendarView.setOnDateChangeListener((@NonNull CalendarView cv, int year, int month, int dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            currentDay = dayOfMonth;
            showMatchesForDay(year, month, dayOfMonth);
        });

        todayButton.setOnClickListener(v -> calendarView.setDate(System.currentTimeMillis()));

        return view;
    }

    private void loadAllMatches() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();
        
        apiServiceConToken.getEvents().enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allMatches = response.body().getData();
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
        
        for (Match match : allMatches) {
            if (match.getScheduled_at() != null && match.getScheduled_at().length() >= 10) {
                String matchDate = match.getScheduled_at().substring(0, 10);
                if (matchDate.equals(selectedDate)) {
                    dayMatches.add(match);
                }
            }
        }
        matchAdapter = new MatchAdapter(dayMatches);
        matchesRecyclerView.setAdapter(matchAdapter);
    }
}
