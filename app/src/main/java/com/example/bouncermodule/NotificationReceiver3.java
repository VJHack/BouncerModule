package com.example.bouncermodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.bouncermodule.ui.authentication.AuthenticationFragment;
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

public class NotificationReceiver3 extends BroadcastReceiver {

    private DatabaseReference mDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Long Action performed");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String currentUser = AuthenticationFragment.getEmail().replaceAll("[.]", "_");
        mDatabase.child("userFeedback").child("Chasers 2_0").child(currentUser).child("lineLength").setValue("Long");
        mDatabase.child("userFeedback").child("Chasers 2_0").child(currentUser).child("time").setValue(System.currentTimeMillis());


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
