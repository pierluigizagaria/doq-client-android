package com.application.doq.notifications.session;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationManagerCompat;

import com.application.doq.App;
import com.application.doq.R;
import com.application.doq.services.SessionService;

public class SessionNotification {

    private static final String TAG = SessionNotification.class.getSimpleName();
    private static final String CHANNEL_ID = App.SESSION_CHANNEL_ID;
    private static final int NOTIFICATION_ID = 1;
    private static final int ICON = R.drawable.ic_baseline_queue_music_24;

    private final Notification.Builder notification;
    private final NotificationManagerCompat notificationManager;

    public SessionNotification(Context context) {
        this.notificationManager = NotificationManagerCompat.from(context);
        this.notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(ICON)
                .setColor(Color.WHITE)
                .setContentTitle(context.getString(R.string.session_notification_title))
                .setSubText(context.getString(R.string.session_notification_substring))
                .setShowWhen(false)
                .setAutoCancel(true)
                .addAction(getShareAction(context))
                .addAction(getCloseAction(context));
    }

    private Notification.Action getShareAction(Context context) {
        return new Notification.Action.Builder(
                null,
                context.getString(R.string.session_notification_invite),
                getShareIntent(context)).build();
    }

    private Notification.Action getCloseAction(Context context) {
        return new Notification.Action.Builder(
                null,
                context.getString(R.string.session_notification_close),
                getCloseIntent(context)).build();
    }

    private PendingIntent getShareIntent(Context context) {
        Intent openSharesheet = new Intent(context, SessionService.class);
        openSharesheet.setAction(SessionService.OPEN_SHARESHEET);
        return PendingIntent.getService(context, 1, openSharesheet, 0);
    }

    private PendingIntent getCloseIntent(Context context) {
        Intent stopGroupService = new Intent(context, SessionService.class);
        stopGroupService.setAction(SessionService.STOP_ACTION);
        return PendingIntent.getService(context, 1, stopGroupService, 0);
    }

    private void updateNotification() {
        this.notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public Notification getNotification() {
        return this.notification.build();
    }

    public void updateContentText(String text) {
        this.notification.setContentText(text);
        this.updateNotification();
    }
}
