package com.adriim1.esports_calendar.receptor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class ReceptorNotis extends BroadcastReceiver {
    private static final String CHANNEL_ID = "esports_matches";
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "¡Alarma recibida! Procesando notificación...");
        
        String matchName = intent.getStringExtra("matchName");
        String message = intent.getStringExtra("message");
        int matchId = intent.getIntExtra("matchId", (int) System.currentTimeMillis());

        if (matchName == null) matchName = "Evento de Esports";
        if (message == null) message = "¡Tu partido está por comenzar!";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, 
                    "Recordatorios de Partidos", 
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones para suscripciones de partidos");
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Usamos uno del sistema por seguridad
                .setContentTitle(matchName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        notificationManager.notify(matchId, builder.build());
        Log.d(TAG, "Notificación enviada al sistema para: " + matchName);
    }
}
