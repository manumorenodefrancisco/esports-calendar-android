package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventoAdapter eventAdapterRV;
    private List<Evento> eventList;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        eventAdapterRV = new EventoAdapter(eventList, false, false);
        recyclerView.setAdapter(eventAdapterRV);

        apiService = RetrofitClient.getApiService(getToken());
        cargarSubscripciones();

        return view;
    }

    private void cargarSubscripciones() {
        Call<ApiService.SubscriptionsResponse> call = apiService.getSubscriptions();
        call.enqueue(new Callback<ApiService.SubscriptionsResponse>() {
            @Override
            public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Suscripcion> suscripciones = response.body().getData();
                    eventList.clear();
                    
                    if (suscripciones != null) {
                        for (Suscripcion sus : suscripciones) {
                            Evento evento = sus.getEvento();
                            if (evento != null) {
                                eventList.add(evento);
                            }
                        }
                    }
                    eventAdapterRV.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
            }
        });
    }

    private String getToken() {
        SharedPreferences prefs = getContext().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        return prefs.getString("accessToken", "");
    }
}
