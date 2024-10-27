package com.example.lab5_20206456;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MotivacionReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "canal_motivacion";
    private static final String[] MENSAJES_MOTIVACION = {
            "¡Sigue así, lo estás haciendo genial!",
            "¡No te rindas, cada paso cuenta!",
            "¡Tú puedes lograr tus objetivos!",
            "¡Mantente enfocado y alcanza tus metas!",
            "¡Cada esfuerzo te acerca más al éxito!"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        int indice = (int) (Math.random() * MENSAJES_MOTIVACION.length);
        String mensaje = MENSAJES_MOTIVACION[indice];
        crearNotificacion(context, mensaje);
    }

    private void crearNotificacion(Context context, String mensaje) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Motivación",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.start)
                .setContentTitle("Motivación")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
