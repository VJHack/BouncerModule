package com.example.bouncermodule;

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
import java.util.HashMap;
import java.util.Map;

public class NotificationReceiver3 extends BroadcastReceiver {
    private DatabaseReference mDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference barRef = mDatabase.child("bars/");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        barRef.child("Chasers 2_0").child("userInput").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    barRef.child("Chasers 2_0").child("userInput").child(timeStamp).setValue("Long");

                }
            }
        });
//        barRef.child("Chasers 2_0").child("userInput").setValue("Long");
        System.out.println("Long Action performed" + intent.getIntExtra("barName",0));
    }


}
