package com.example.proyecto_android;

import android.app.Application;

import com.example.proyecto_android.service.ChatNotificationManager;

public class ProyectoAndroidApplication extends Application {

    private ChatNotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager =
                new ChatNotificationManager(this);

        notificationManager.start();
    }

    public void startChatNotifications() {

        if (notificationManager != null) {
            notificationManager.start();
        }
    }

    public void stopChatNotifications() {

        if (notificationManager != null) {
            notificationManager.stop();
        }
    }
}