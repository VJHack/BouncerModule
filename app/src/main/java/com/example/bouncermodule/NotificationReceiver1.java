package com.example.bouncermodule;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationReceiver1 extends BroadcastReceiver {

    private NotificationManager mNotifyManager;
    private DatabaseReference mDatabase;

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
                    DatabaseReference barRef = mDatabase.child("bars/");

                    barRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            List<String> list = new ArrayList<>();
                            for(DataSnapshot data: task.getResult().getChildren()){
                                String barName = data.getKey();
                                barRef.child(intent.getStringExtra("barName")).child("userInput").child(timeStamp).setValue("Short");
                                if(data.child("userInput").getChildrenCount() > 4) {
                                    for (DataSnapshot inputs : data.child("userInput").getChildren()) {
                                        System.out.println(barRef.child(intent.getStringExtra("barName")).child("userInput").child(inputs.getKey()));
                                        barRef.child(intent.getStringExtra("barName")).child("userInput").child(inputs.getKey()).removeValue();
                                        break;
                                    }
                                }
                            }
                        }



                    });

                }
            }
        });
        System.out.println("Short Action performed"+ intent.getStringExtra("barName"));
    }
}
