package com.example.subscribe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import static com.example.subscribe.AddSubscriptionActivity.NOTIFICATION_CHANNEL_ID;

public class Reminder extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";


    // Generates a notification via a notification channel. Extends broadcastreceiver.

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager remManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification reminder = intent.getParcelableExtra(NOTIFICATION);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel reminderChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME",importance);
            assert remManager != null;
            remManager.createNotificationChannel(reminderChannel);
        }
        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        assert remManager != null;
        remManager.notify(id,reminder);
    }
}