package com.example.proyecto_android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.proyecto_android.model.Message;
import com.example.proyecto_android.view.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ChatNotificationManager {

    private static final String CHANNEL_ID = "chat_messages";

    private final Context context;
    private final FirebaseFirestore firestore;

    private ListenerRegistration messageListener;

    public ChatNotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void start() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        stop();

        String currentUserId = currentUser.getUid();

        final boolean[] firstSnapshot = {true};

        messageListener =
                firestore.collectionGroup("messages")
                        .whereEqualTo(
                                "receiverId",
                                currentUserId
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null ||
                                            snapshot == null) {
                                        return;
                                    }

                                    if (firstSnapshot[0]) {
                                        firstSnapshot[0] = false;
                                        return;
                                    }

                                    for (DocumentChange change :
                                            snapshot.getDocumentChanges()) {

                                        if (change.getType() !=
                                                DocumentChange.Type.ADDED) {
                                            continue;
                                        }

                                        Message message =
                                                change.getDocument()
                                                        .toObject(
                                                                Message.class
                                                        );

                                        showNotification(message);
                                    }
                                }
                        );
    }

    public void stop() {

        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
        }
    }

    private void showNotification(Message message) {

        if (message == null ||
                message.getSenderId() == null) {
            return;
        }

        firestore.collection("users")
                .document(message.getSenderId())
                .get()
                .addOnSuccessListener(document -> {

                    String senderName = "Usuario";

                    if (document.exists()) {

                        String name =
                                document.getString("name");

                        if (name != null &&
                                !name.trim().isEmpty()) {

                            senderName = name;
                        }
                    }

                    createNotification(
                            message,
                            senderName
                    );
                });
    }

    private void createNotification(
            Message message,
            String senderName
    ) {

        createNotificationChannel();

        String body = message.getText();

        if (body == null ||
                body.trim().isEmpty()) {

            if (message.getImageBase64() != null &&
                    !message.getImageBase64()
                            .trim()
                            .isEmpty()) {

                body = "Te envió una imagen";

            } else {

                body = "Tienes un mensaje nuevo";
            }
        }

        Intent intent =
                new Intent(
                        context,
                        ChatActivity.class
                );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_ID,
                message.getSenderId()
        );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_NAME,
                senderName
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_email
                        )
                        .setContentTitle(
                                "Nuevo mensaje de " +
                                        senderName
                        )
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
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
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
                    context.getSystemService(
                            NotificationManager.class
                    );

            manager.createNotificationChannel(channel);
        }
    }
}