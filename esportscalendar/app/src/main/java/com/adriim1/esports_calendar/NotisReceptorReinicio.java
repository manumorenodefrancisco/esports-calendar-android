package com.adriim1.esports_calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotisReceptorReinicio extends BroadcastReceiver {
    // 1. Obtener el token de SharedPreferences
    // 2. Llamar a apiService.getSubscriptions()
    // 3. Recorrer la lista y llamar a la lógica de programarAlarma() que creamos en el adaptador.
    private static final String TAG = "NotisReceptorReinicio";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Reprogramando alarmas tras reinicio...");
            reprogramarAlarmas(context);
        }
    }

    private void reprogramarAlarmas(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) return;

        ApiService apiService = RetrofitClient.getApiService(token);
        apiService.getSubscriptions().enqueue(new Callback<ApiService.SubscriptionsResponse>() {
            @Override
            public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Suscripcion> suscripciones = response.body().getData();
                    if (suscripciones != null) {
                        for (Suscripcion sus : suscripciones) {
                            programarAlarmaParaSuscripcion(context, sus);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                Log.e(TAG, "Error al recuperar suscripciones tras reinicio", t);
            }
        });
    }

    private void programarAlarmaParaSuscripcion(Context context, Suscripcion sus) {
        Evento evento = sus.getEvento();
        if (evento == null || evento.getScheduled_at() == null) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dateEvento = sdf.parse(evento.getScheduled_at());
            if (dateEvento == null) return;

            long timeEventoMillis = dateEvento.getTime();
            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Partido: " + evento.getMatch_name() + " es a las: " + dateEvento.toString());

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String matchName = evento.getMatch_name() != null ? evento.getMatch_name() : "Partido";

            if (sus.isRecordatorio_1_hora()) {
                long trigger = timeEventoMillis - (60 * 60 * 1000);
                if (trigger > currentTime) setAlarm(context, alarmManager, trigger, evento.getId() * 10 + 1, matchName, "¡Empieza en 1 hora!");
            }
            if (sus.isRecordatorio_5_minutos()) {
                long trigger = timeEventoMillis - (5 * 60 * 1000);
                if (trigger > currentTime) setAlarm(context, alarmManager, trigger, evento.getId() * 10 + 2, matchName, "¡Empieza en 5 minutos!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private void setAlarm(Context context, AlarmManager am, long time, int id, String name, String msg) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("matchName", name);
        intent.putExtra("message", msg);
        intent.putExtra("matchId", id);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }
}