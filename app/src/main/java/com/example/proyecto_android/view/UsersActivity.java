package com.example.proyecto_android.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.adapter.UserAdapter;
import com.example.proyecto_android.model.Message;
import com.example.proyecto_android.model.User;
import com.example.proyecto_android.viewmodel.AuthViewModel;
import com.example.proyecto_android.viewmodel.UsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "chat_messages";

    private RecyclerView recyclerUsers;
    private Button btnLogout;

    private UserAdapter userAdapter;

    private AuthViewModel authViewModel;
    private UsersViewModel usersViewModel;

    private String currentUserId;

    private final Map<String, String> userNames =
            new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users);

        requestNotificationPermission();

        initializeViews();

        if (!loadCurrentUser()) {
            return;
        }

        setupRecyclerView();
        setupViewModels();
        setupLogout();
    }

    private void initializeViews() {

        recyclerUsers =
                findViewById(R.id.recyclerUsers);

        btnLogout =
                findViewById(R.id.btnLogout);
    }

    private boolean loadCurrentUser() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (currentUser == null) {

            goToLogin();

            return false;
        }

        currentUserId =
                currentUser.getUid();

        return true;
    }

    private void setupRecyclerView() {

        userAdapter = new UserAdapter(
                new ArrayList<>(),
                this::openChat
        );

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerUsers.setAdapter(userAdapter);
    }

    private void setupViewModels() {

        authViewModel =
                new ViewModelProvider(this)
                        .get(AuthViewModel.class);

        usersViewModel =
                new ViewModelProvider(this)
                        .get(UsersViewModel.class);

        usersViewModel.getUsers().observe(
                this,
                users -> {

                    if (users != null) {

                        userAdapter.updateUsers(users);

                        userNames.clear();

                        for (User user : users) {

                            if (user.getUid() != null) {

                                userNames.put(
                                        user.getUid(),
                                        user.getName() == null
                                                ? "Usuario"
                                                : user.getName()
                                );
                            }
                        }
                    }
                }
        );

        usersViewModel.getErrorMessage().observe(
                this,
                error -> {

                    if (error == null ||
                            error.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            error,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        usersViewModel.getIncomingMessage().observe(
                this,
                this::showIncomingMessageNotification
        );

        usersViewModel.startListeningForUsers(
                currentUserId
        );

        usersViewModel.startListeningForIncomingMessages(
                currentUserId
        );

        usersViewModel.syncFcmToken(
                currentUserId
        );
    }

    private void setupLogout() {

        btnLogout.setOnClickListener(view -> {

            authViewModel.logout();

            goToLogin();
        });
    }

    private void openChat(User user) {

        if (user == null ||
                user.getUid() == null ||
                user.getUid().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "No se pudo abrir la conversación.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        UsersActivity.this,
                        ChatActivity.class
                );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_ID,
                user.getUid()
        );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_NAME,
                user.getName()
        );

        startActivity(intent);
    }

    private void goToLogin() {

        Intent intent =
                new Intent(
                        UsersActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    private void showIncomingMessageNotification(
            Message message) {

        if (message == null ||
                message.getSenderId() == null) {

            return;
        }

        createNotificationChannel();

        String senderName =
                userNames.get(message.getSenderId());

        if (senderName == null ||
                senderName.trim().isEmpty()) {

            senderName = "Usuario";
        }

        String body =
                message.getText();

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
                        UsersActivity.this,
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
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
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
                                new NotificationCompat
                                        .BigTextStyle()
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

            manager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        1001
                );
            }
        }
    }
}