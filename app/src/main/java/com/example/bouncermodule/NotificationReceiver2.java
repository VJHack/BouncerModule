package com.example.bouncermodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NotificationReceiver2 extends BroadcastReceiver {

    private DatabaseReference mDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Medium Action performed");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String tempName = "Tempuser" + String.valueOf(ThreadLocalRandom.current().nextInt(0, 5 + 1));
        mDatabase.child("userFeedback").child("Chasers 2_0").child(tempName).child("lineLength").setValue("Medium");
        mDatabase.child("userFeedback").child("Chasers 2_0").child(tempName).child("time").setValue(System.currentTimeMillis());


        DatabaseReference myRef = mDatabase.child("userFeedback").child("Chasers 2_0");
        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        List<String[]> list = new ArrayList<>();
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            String username = data.getKey();
                            String time = data.child("time").getValue().toString();
                            String[] tempFeedback = {username, time};
                            list.add(tempFeedback);
                        }
                        if (list.size() > 5) {
                            Collections.sort(list,new Comparator<String[]>() {
                                public int compare(String[] strings, String[] otherStrings) {
                                    return strings[1].compareTo(otherStrings[1]);
                                }
                            });

                            mDatabase.child("userFeedback").child("Chasers 2_0").child(list.get(0)[0]).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
}
