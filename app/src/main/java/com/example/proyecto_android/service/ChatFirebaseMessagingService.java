package com.example.proyecto_android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.proyecto_android.repository.UserRepository;
import com.example.proyecto_android.view.ChatActivity;
import com.example.proyecto_android.view.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ChatFirebaseMessagingService
        extends FirebaseMessagingService {

    private static final String CHANNEL_ID =
            "chat_messages";

    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);

        android.util.Log.d(
                "FCM_DEBUG",
                "Nuevo token: " + token
        );

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

                        android.util.Log.d(
                                "FCM_DEBUG",
                                "Token actualizado en Firestore"
                        );
                    }

                    @Override
                    public void onError(String message) {

                        android.util.Log.e(
                                "FCM_DEBUG",
                                "Error guardando token: " + message
                        );
                    }
                }
        );
    }

    @Override
    public void onMessageReceived(
            @NonNull RemoteMessage remoteMessage
    ) {

        super.onMessageReceived(remoteMessage);

        android.util.Log.d(
                "FCM_DEBUG",
                "Mensaje FCM recibido: "
                        + remoteMessage.getData()
        );

        String title = "Nuevo mensaje";
        String body = "Tienes un mensaje nuevo";

        String senderId = null;
        String senderName = null;

        Map<String, String> data =
                remoteMessage.getData();

        if (!data.isEmpty()) {

            senderId = data.get("senderId");
            senderName = data.get("senderName");

            String receivedTitle =
                    data.get("title");

            String receivedBody =
                    data.get("body");

            if (receivedTitle != null &&
                    !receivedTitle.trim().isEmpty()) {

                title = receivedTitle;
            }

            if (receivedBody != null &&
                    !receivedBody.trim().isEmpty()) {

                body = receivedBody;
            }
        }

        // Fallback por si algún día se recibe
        // una notificación tradicional de FCM.
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

        showNotification(
                title,
                body,
                senderId,
                senderName
        );
    }

    private void showNotification(
            String title,
            String body,
            String senderId,
            String senderName
    ) {

        createNotificationChannel();

        Intent intent;

        if (senderId != null &&
                !senderId.trim().isEmpty()) {

            // Si sabemos quién envió el mensaje,
            // al tocar la notificación abrimos
            // directamente su conversación.
            intent = new Intent(
                    this,
                    ChatActivity.class
            );

            intent.putExtra(
                    ChatActivity.EXTRA_RECEIVER_ID,
                    senderId
            );

            intent.putExtra(
                    ChatActivity.EXTRA_RECEIVER_NAME,
                    senderName
            );

        } else {

            // Si por alguna razón no llegó senderId,
            // enviamos al listado de usuarios.
            intent = new Intent(
                    this,
                    UsersActivity.class
            );
        }

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        int requestCode;

        if (senderId != null) {
            requestCode = senderId.hashCode();
        } else {
            requestCode =
                    (int) System.currentTimeMillis();
        }

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        requestCode,
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
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(body)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setCategory(
                                NotificationCompat.CATEGORY_MESSAGE
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