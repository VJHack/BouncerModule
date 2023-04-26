package com.example.bouncermodule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bouncermodule.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long START_HANDLER_DELAY = 10;
    private ActivityMainBinding binding;
    private TextView counterValue;
    private TextView currentLength;
    private Button noneButton;
    private Button shortButton;
    private Button mediumButton;
    private Button longButton;

    private Button plusButton;
    private Button minusButton;

    // Grab value from database based on desired bar
    private int counterValInt = 0;

    private ImageView photoIdImageView;
    private Button photoIdButton;

    private NotificationManager mNotifyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        sendNotification(navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_bars)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

    // used for reading in file filled with new bars
//    public void writeNewBar(String barId, String lineLength, Integer lineCount, Double longitude, Double latitude) {
//        if (barsMap.containsKey(barId) == false){
//            Bars bar = new Bars(lineLength, lineCount, longitude, latitude);
//            Log.d("Writing new Bar", barId);
//            barsMap.put(barId, bar);
//            mDatabase.child("bars").child(barId).setValue(bar);
//        }
//    }


    // This ID can be the value you want.
    private static final int NOTIFICATION_ID = 0;

    // This ID can be the value you want.
    private static final String NOTIFICATION_ID_STRING = "My Notifications";

    public void sendNotification(View view) {

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Create the channel. Android will automatically check if the channel already exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID_STRING, "My Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My notification channel description");
            mNotifyManager.createNotificationChannel(channel);
        }

        //Intent for slow button
        Intent broadcastIntent1 = new Intent(this, NotificationReceiver1.class);
        broadcastIntent1.putExtra("toastMessage1", "message");
        PendingIntent actionIntent1 = PendingIntent.getBroadcast(this, 0, broadcastIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Intent for medium button
        Intent broadcastIntent2 = new Intent(this, NotificationReceiver2.class);
        broadcastIntent2.putExtra("toastMessage1", "message");
        PendingIntent actionIntent2 = PendingIntent.getBroadcast(this, 0, broadcastIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Intent for long button
        Intent broadcastIntent3 = new Intent(this, NotificationReceiver3.class);
        broadcastIntent3.putExtra("toastMessage1", "message");
        PendingIntent actionIntent3 = PendingIntent.getBroadcast(this, 0, broadcastIntent3, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Setting the Color of each Notification button
        Spannable spannableShort = new SpannableString("Short");
        spannableShort.setSpan(new ForegroundColorSpan(Color.rgb(0,128,0)), 0, spannableShort.length(), 0);
        Spannable spannableMed = new SpannableString("Medium");
        spannableMed.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMed.length(), 0);
        Spannable spannableLong = new SpannableString("Long");
        spannableLong.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableLong.length(), 0);

        NotificationCompat.Builder notifyBuilder
                = new NotificationCompat.Builder(this, NOTIFICATION_ID_STRING)
                .setContentTitle("You're near a bar!")
                .setContentText("How long is the line at Chasers 2.0?")
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .addAction(R.drawable.ic_home_black_24dp,  spannableShort, actionIntent1)
                .addAction(R.drawable.ic_home_black_24dp, spannableMed, actionIntent2)
                .addAction(R.drawable.ic_home_black_24dp, spannableLong, actionIntent3)
                ;
        ;


        Notification myNotification = notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, myNotification);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.Short) {
            currentLength.setText("SHORT");
            currentLength.setTextColor(Color.parseColor("#0000FF"));
        }else if(view.getId() == R.id.Medium){
            currentLength.setText("MEDIUM");
            currentLength.setTextColor(Color.parseColor("#FFA500"));
        }else if(view.getId() == R.id.Long){
            currentLength.setText("LONG");
            currentLength.setTextColor(Color.parseColor("#FF0000")); // Color Red
        }else if(view.getId() == R.id.None){
            currentLength.setText("NONE");
            currentLength.setTextColor(Color.parseColor("#028A0F"));
        }else if(view.getId() == R.id.plus){
            counterValInt++;
            counterValue.setText("Total:    " +String.valueOf(counterValInt));
        }else if(view.getId() == R.id.minus){
            if(counterValInt >= 1) {
                counterValInt--;
            }
            counterValue.setText("Total:    " + String.valueOf(counterValInt));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            photoIdImageView.setImageBitmap(bitmap);
        }
    }

}