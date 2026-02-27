package com.adriim1.esports_calendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    private static final String TAG = "FCMService";
    
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Token FCM: " + token);
        enviarTokenAlBackend(token);
    }
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Mensaje recibido: " + remoteMessage.getData());
        
        String titulo = remoteMessage.getData().get("titulo");
        String mensaje = remoteMessage.getData().get("mensaje");
        String eventoNombre = remoteMessage.getData().get("evento_nombre");
        String eventoId = remoteMessage.getData().get("evento_id");
        
        if (titulo == null) titulo = "Notificación";
        if (mensaje == null) mensaje = "Tienes una nueva notificación";
        if (eventoNombre == null) eventoNombre = "Evento";
        
        agregarNotificacionAlBackend(titulo, mensaje, eventoNombre,
            eventoId != null ? Integer.parseInt(eventoId) : 0);
        
        mostrarNotificacion(remoteMessage);
    }
    
    private void enviarTokenAlBackend(String token) {
        SharedPreferences prefs = getSharedPreferences("EsportsCalendarPrefs", MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        
        if (accessToken != null) {
            ApiService apiServiceConToken = RetrofitClient.getApiService(accessToken);
            ApiService.TokenRequest tokenRequest = new ApiService.TokenRequest(token);
            
            apiServiceConToken.registerNotificationToken(tokenRequest).enqueue(new retrofit2.Callback<ApiResponse>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Log.d(TAG, "Token FCM registrado exitosamente");
                    } else {
                        Log.e(TAG, "Error registrando token FCM: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "Error de red registrando token FCM", t);
                }
            });
        }
    }
    
    private void agregarNotificacionAlBackend(String titulo, String mensaje, String eventoNombre, int eventoId) {
        SharedPreferences prefs = getSharedPreferences("EsportsCalendarPrefs", MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        
        if (accessToken == null) return;
        
        ApiService apiServiceConToken = RetrofitClient.getApiService(accessToken);
        
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("titulo", titulo);
        notificationData.put("mensaje", mensaje);
        notificationData.put("evento_nombre", eventoNombre);
        notificationData.put("evento_id", String.valueOf(eventoId));
        
        apiServiceConToken.addNotification(notificationData).enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "Notificación agregada al backend");
                } else {
                    Log.e(TAG, "Error agregando notificación al backend");
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error de red agregando notificación", t);
            }
        });
    }
    
    private void mostrarNotificacion(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        
        if (title == null) title = "Notificación";
        if (body == null) body = "Tienes una nueva notificación";
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel",
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        
        manager.notify(0, builder.build());
    }
}
