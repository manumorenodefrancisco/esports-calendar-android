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
    private RecyclerView subscriptionsRecyclerView;
    private EventoAdapter subscriptionAdapter;
    private List<Evento> subscriptionEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        subscriptionsRecyclerView = view.findViewById(R.id.recycler_view_notifications);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        subscriptionEvents = new ArrayList<>();
        subscriptionAdapter = new EventoAdapter(subscriptionEvents);
        subscriptionsRecyclerView.setAdapter(subscriptionAdapter);
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
                    List<Suscripcion> suscripciones = response.body().getData();
                    subscriptionEvents = new ArrayList<>();
                    
                    for (Suscripcion sus : suscripciones) {
                        Evento evento = sus.getEvento();
                        if (evento != null) {
                            subscriptionEvents.add(evento);
                        }
                    }
                    
                    subscriptionAdapter.getEventoList().clear();
                    subscriptionAdapter.getEventoList().addAll(subscriptionEvents);
                    subscriptionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
            }
        });
    }
}
