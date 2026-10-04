package com.example.proyecto_android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.proyecto_android.R;
import com.example.proyecto_android.repository.UserRepository;
import com.example.proyecto_android.view.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatFirebaseMessagingService
        extends FirebaseMessagingService {

    private static final String CHANNEL_ID =
            "chat_messages";

    @Override
    public void onNewToken(@NonNull String token) {
        android.util.Log.d(
                "FCM_DEBUG",
                "Nuevo token: " + token
        );
        super.onNewToken(token);

        FirebaseUser currentUser =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (currentUser == null) {
            return;
        }

        new UserRepository().saveFcmToken(
                currentUser.getUid(),
                token,
                new UserRepository.OperationCallback() {

                    @Override
                    public void onSuccess() {
                        // Token actualizado
                    }

                    @Override
                    public void onError(String message) {
                        // Se volverá a sincronizar
                        // cuando el usuario entre a la app
                    }
                }
        );
    }

    @Override
    public void onMessageReceived(
            @NonNull RemoteMessage remoteMessage
    ) {
        android.util.Log.d(
                "FCM_DEBUG",
                "Mensaje FCM recibido"
        );
        super.onMessageReceived(remoteMessage);

        String title = "Nuevo mensaje";
        String body = "Tienes un mensaje nuevo";

        if (remoteMessage.getNotification() != null) {

            if (remoteMessage
                    .getNotification()
                    .getTitle() != null) {

                title = remoteMessage
                        .getNotification()
                        .getTitle();
            }

            if (remoteMessage
                    .getNotification()
                    .getBody() != null) {

                body = remoteMessage
                        .getNotification()
                        .getBody();
            }
        }

        showNotification(title, body);
    }

    private void showNotification(
            String title,
            String body
    ) {

        createNotificationChannel();

        Intent intent =
                new Intent(
                        this,
                        UsersActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_email
                        )
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        manager.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Mensajes del chat",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Notificaciones de mensajes nuevos"
            );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            manager.createNotificationChannel(
                    channel
            );
        }
    }
}