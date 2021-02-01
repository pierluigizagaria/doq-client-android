package com.application.doq.services;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.application.doq.notifications.session.StartSessionNotification;

public class SpotifyNotificationListener extends NotificationListenerService {

    private static final String TAG = SpotifyNotificationListener.class.getSimpleName();
    private static final String SPOTIFY_PACKAGE_NAME = "com.spotify.music";
    private static boolean playbackNotification;

    private StartSessionNotification startSessionNotification;

    public SpotifyNotificationListener() { super(); }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        startSessionNotification = new StartSessionNotification(this);
        this.getPlaybackNotificationStatus();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!playbackNotification && isPlaybackNotification(sbn)) {
            playbackNotification = true;
            this.onPlaybackNotification(sbn);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (playbackNotification && isPlaybackNotification(sbn)) {
            playbackNotification = false;
            this.onDismiss(sbn);
        }
    }

    private void getPlaybackNotificationStatus() {
        StatusBarNotification foundNotification = findPlaybackNotification(this.getActiveNotifications());
        if (foundNotification != null) {
            playbackNotification = true;
            this.onPlaybackNotification(foundNotification);
        }
    }

    private boolean isPlaybackNotification(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String category = sbn.getNotification().category;
        if (packageName == null || category == null) return false;
        return packageName.equals(SPOTIFY_PACKAGE_NAME) && category.equals(Notification.CATEGORY_TRANSPORT);
    }

    private StatusBarNotification findPlaybackNotification(StatusBarNotification[] sbnArray) {
        for(StatusBarNotification sbn : sbnArray) { if (isPlaybackNotification(sbn)) return sbn; }
        return null;
    }

    private void onPlaybackNotification(StatusBarNotification sbn) { startSessionNotification.show(); }

    private void onDismiss(StatusBarNotification sbn) {
        startSessionNotification.dismiss();
    }
}
