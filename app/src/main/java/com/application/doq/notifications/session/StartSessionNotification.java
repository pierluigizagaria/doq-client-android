package com.application.doq.notifications.session;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.application.doq.App;
import com.application.doq.R;
import com.application.doq.services.SessionService;

public class StartSessionNotification {

    private static final String TAG = StartSessionNotification.class.getSimpleName();
    private static final String CHANNEL_ID = App.SESSION_CHANNEL_ID;
    private static final int NOTIFICATION_ID = 1;
    private static final int ICON = R.drawable.ic_baseline_queue_music_24;
    private static final int NOTIFICATION_TIME = 20 * 1000;

    private final Notification.Builder notification;
    private final NotificationManagerCompat notificationManager;

    public StartSessionNotification(Context context) {
        notificationManager = NotificationManagerCompat.from(context);
        notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(ICON)
                .setContentTitle(context.getString(R.string.start_session_notification_title))
                .setContentText(context.getString(R.string.start_session_notification_text))
                .setShowWhen(false)
                .setAutoCancel(false)
                .setTimeoutAfter(NOTIFICATION_TIME)
                .setContentIntent(getPendingIntent(context));
    }

    public void show(){
        notificationManager.notify(NOTIFICATION_ID, this.notification.build());
    }

    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent startSessionIntent = new Intent(context, SessionService.class);
        return PendingIntent.getService(context, 1, startSessionIntent, 0);
    }
}
