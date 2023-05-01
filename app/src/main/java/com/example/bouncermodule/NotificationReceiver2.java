package com.example.bouncermodule;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationReceiver2 extends BroadcastReceiver {
    private DatabaseReference mDatabase;
    private NotificationManager mNotifyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.cancel(intent.getIntExtra("notifId", -1));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference barRef = mDatabase.child("bars/");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        barRef.child(intent.getStringExtra("barName")).child("userInput").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    System.out.println(timeStamp);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                    Map<String, String> timeMap = new HashMap<>();
//                    timeMap.put(timeStamp, "Long");
//                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//                    Date past = format.parse("01/10/2010");
//                    Date now = new Date();
//                    TimeUnit.MILLISECONDS.toMinutes
                    barRef.child(intent.getStringExtra("barName")).child("userInput").child(timeStamp).setValue("Medium");

                }
            }
        });
        System.out.println("Medium Action performed"+ intent.getStringExtra("barName"));
    }
}
