package com.application.doq;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import java.util.ArrayList;

public class App extends Application {

    public static final String SESSION_CHANNEL_ID = "sessionNotification";
    public static final String SESSION_INFO_CHANNEL_ID = "sessionEventNotification";

    private final ArrayList<NotificationChannel> notificationChannels = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        notificationChannels.add(getSessionNotificationChannel());
        notificationChannels.add(getSessionEventNotificationChannel());
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannels(notificationChannels);
    }

    private NotificationChannel getSessionNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                SESSION_CHANNEL_ID,
                getString(R.string.session_notification),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setSound(null, null);
        return channel;
    }

    private NotificationChannel getSessionEventNotificationChannel() {
        return new NotificationChannel(
                SESSION_INFO_CHANNEL_ID,
                getString(R.string.session_event_notification),
                NotificationManager.IMPORTANCE_HIGH
        );
    }
}
