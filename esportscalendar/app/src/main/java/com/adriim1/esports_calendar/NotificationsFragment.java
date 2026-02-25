package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";
    private RecyclerView notificationsRecyclerView;
    private MatchAdapter notificationAdapter;
    private List<Match> subscribedMatches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsRecyclerView = view.findViewById(R.id.recycler_view_notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        subscribedMatches = new ArrayList<>();
        loadSubscribedEvents();

        return view;
    }

    private void loadSubscribedEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        if (token == null) return;
        
        ApiService apiServiceWithToken = RetrofitClient.getApiService(token);
        apiServiceWithToken.getSubscriptions().enqueue(new Callback<ApiService.SubscriptionsResponse>() {
            @Override
            public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subscribedMatches = response.body().getData();
                    notificationAdapter = new MatchAdapter(subscribedMatches);
                    notificationsRecyclerView.setAdapter(notificationAdapter);
                }
            }

            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
            }
        });
    }
}
