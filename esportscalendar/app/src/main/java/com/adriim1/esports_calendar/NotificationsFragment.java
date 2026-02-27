package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private RecyclerView subscriptionsRecyclerView;
    private EventoAdapter subscriptionAdapter;
    private List<Evento> subscriptionEvents;
    private LinearLayout emptyStateLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        subscriptionsRecyclerView = view.findViewById(R.id.recycler_view_notifications);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);
        
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        subscriptionEvents = new ArrayList<>();
        subscriptionAdapter = new EventoAdapter(subscriptionEvents);
        subscriptionsRecyclerView.setAdapter(subscriptionAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscribedEvents();
    }

    private void loadSubscribedEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        if (token == null) {
            updateEmptyState(true);
            return;
        }
        
        ApiService apiServiceWithToken = RetrofitClient.getApiService(token);
        apiServiceWithToken.getSubscriptions().enqueue(new Callback<ApiService.SubscriptionsResponse>() {
            @Override
            public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Suscripcion> suscripciones = response.body().getData();
                    subscriptionEvents.clear();
                    
                    if (suscripciones != null) {
                        for (Suscripcion sus : suscripciones) {
                            Evento evento = sus.getEvento();
                            if (evento != null) {
                                subscriptionEvents.add(evento);
                            }
                        }
                    }
                    
                    subscriptionAdapter.notifyDataSetChanged();
                    updateEmptyState(subscriptionEvents.isEmpty());
                } else {
                    updateEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
                updateEmptyState(true);
            }
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (subscriptionsRecyclerView != null) {
            subscriptionsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }
}
