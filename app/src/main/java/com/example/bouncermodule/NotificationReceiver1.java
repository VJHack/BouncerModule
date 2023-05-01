package com.example.bouncermodule;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver1 extends BroadcastReceiver {

    private NotificationManager mNotifyManager;
    @Override

    public void onReceive(Context context, Intent intent) {
        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.cancel(intent.getIntExtra("notifId", -1));
        System.out.println("Short Action performed"+ intent.getStringExtra("barName"));
    }
}
