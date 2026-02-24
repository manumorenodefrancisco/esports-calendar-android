package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private RecyclerView notificationsRecyclerView;
    private MatchAdapter notificationAdapter;
    private List<Match> subscribedMatches;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        notificationsRecyclerView = findViewById(R.id.recycler_view_notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        apiService = RetrofitClient.getApiService();
        subscribedMatches = new ArrayList<>();

        loadSubscribedEvents();
        
        // Navegación
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
    }

    private void loadSubscribedEvents() {
        SharedPreferences prefs = getSharedPreferences("EsportsCalendarPrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        
        if (token == null) {
            Log.e(TAG, "No hay token de autenticación");
            return;
        }
        
        ApiService apiServiceWithToken = RetrofitClient.getApiService(token);
        
        apiServiceWithToken.getSubscriptions().enqueue(new Callback<ApiService.SubscriptionsResponse>() {
            @Override
            public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subscribedMatches = response.body().getData();
                    Log.d(TAG, "Cargadas " + subscribedMatches.size() + " suscripciones");
                    
                    // Actualizar RecyclerView
                    notificationAdapter = new MatchAdapter(subscribedMatches);
                    notificationsRecyclerView.setAdapter(notificationAdapter);
                    
                } else {
                    Log.e(TAG, "Error cargando suscripciones - Response: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Success: " + response.body().isSuccess());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión cargando suscripciones", t);
            }
        });
    }
}
