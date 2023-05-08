package com.example.bouncermodule;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouncermodule.ui.bars.BarsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long START_HANDLER_DELAY = 10;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "true";
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
    public boolean requestingLocationUpdates =  true;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    LocationRequest locationRequest;


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;

    public LocationCallback locationCallback;


    //Current Location
    //TODO: Replace these values with realtime values
    private double lat = -89.3876907;
    private double lon = 43.0686899;

    private DatabaseReference mDatabase;

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    Geocoder geocoder;

    HashMap<String, String> barNotifsSent = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = new Intent(getApplicationContext(), GoogleService.class);
        startService(intent);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//        checkLocation(navView);

        setContentView(binding.getRoot());

        DatabaseReference barRef = mDatabase.child("bars/");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_bars)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };
//        updateValuesFromBundle(savedInstanceState);
//
//        startLocationUpdates();


    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));


            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                System.out.println("Latitude Bckgnd: "+ latitude);
                System.out.println("Longitude Bckng: "+ longitude);
//                tv_area.setText(addresses.get(0).getAdminArea());
//                tv_locality.setText(stateName);
//                tv_address.setText(countryName);

                lat = longitude;
                lon = latitude;

                BottomNavigationView navView = findViewById(R.id.nav_view);
                checkLocation(navView);

            } catch (IOException e1) {
                e1.printStackTrace();
            }


//            tv_latitude.setText(latitude+"");
//            tv_longitude.setText(longitude+"");
//            tv_address.getText();


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(broadcastReceiver);
    }
//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        System.out.println("aaaaaaaa");
//        if (savedInstanceState == null) {
//            return;
//        }
//
//        // Update the value of requestingLocationUpdates from the Bundle.
//        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//            requestingLocationUpdates = savedInstanceState.getBoolean(
//                    REQUESTING_LOCATION_UPDATES_KEY);
//        }
//
//        // ...
//
//        // Update UI to match restored state
//
//    }


//    @SuppressLint("MissingPermission")
//    private void getLastLocation() {
//        // check if permissions are given
//        if (checkPermissions()) {
//
//            // check if location is enabled
//            if (isLocationEnabled()) {
//
//                // getting last
//                // location from
//                // FusedLocationClient
//                // object
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        Location location = task.getResult();
//                        if (location == null) {
//                            requestNewLocationData();
//                        } else {
//                            System.out.println(location.getLatitude());
//                            System.out.println(location.getLongitude());
//                            location=null;
////                            latitudeTextView.setText(location.getLatitude() + "");
////                            longitTextView.setText(location.getLongitude() + "");
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            // if permissions aren't available,
//            // request for permissions
//            requestPermissions();
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData() {
//
//        // Initializing LocationRequest
//        // object with appropriate methods
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(5);
//        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setNumUpdates(1);
//
//        // setting LocationRequest
//
//        // on FusedLocationClient
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
//    }
//
//
//
//    // method to check for permissions
//    private boolean checkPermissions() {
//        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//        // If we want background location
//        // on Android 10.0 and higher,
//        // use:
//        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }
//
//    private void stopLocationUpdates() {
//        mFusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    // method to request for permissions
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this, new String[]{
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
//    }
//
//    // method to check
//    // if location is enabled
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    // If everything is alright then
//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            }
//        }
//    }
//
//    protected void onResume() {
//        super.onResume();
//        if (requestingLocationUpdates) {
//            startLocationUpdates();
//        }
//    }
//
//    private void startLocationUpdates() {
//        System.out.println("fewrfwefwef");
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mFusedLocationClient.requestLocationUpdates(locationRequest,
//                locationCallback,
//                Looper.getMainLooper());
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        boolean requestingLocationUpdates = true;
//        outState.putBoolean(String.valueOf(true),
//                requestingLocationUpdates);
//        // ...
//        super.onSaveInstanceState(outState);
//    }

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

    // This ID can be the value you want.
    private static final String NOTIFICATION_ID_STRING = "My Notifications";

    public void sendNotification(View view, String barName) {
        int NOTIFICATION_ID = (int)Math.round(Math.random()*100);
        System.out.println("NOTIFICATION_ID"+NOTIFICATION_ID);
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Random randInt = new Random();

        //Create the channel. Android will automatically check if the channel already exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID_STRING, "My Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My notification channel description");
            mNotifyManager.createNotificationChannel(channel);
        }

        //Intent for slow button
        Intent broadcastIntent1 = new Intent(this, NotificationReceiver1.class);

        broadcastIntent1.putExtra("barName", barName);
        broadcastIntent1.putExtra("notifId", NOTIFICATION_ID);
        PendingIntent actionIntent1 = PendingIntent.getBroadcast(this, randInt.ints(1, 101).findAny().getAsInt(), broadcastIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Intent for medium button
        Intent broadcastIntent2 = new Intent(this, NotificationReceiver2.class);
        broadcastIntent2.putExtra("barName", barName);
        broadcastIntent2.putExtra("notifId", NOTIFICATION_ID);
        PendingIntent actionIntent2 = PendingIntent.getBroadcast(this, randInt.ints(1, 101).findAny().getAsInt(), broadcastIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Intent for long button
        Intent broadcastIntent3 = new Intent(this, NotificationReceiver3.class);
        broadcastIntent3.putExtra("barName", barName);
        broadcastIntent3.putExtra("notifId", NOTIFICATION_ID);
        PendingIntent actionIntent3 = PendingIntent.getBroadcast(this, randInt.ints(1, 101).findAny().getAsInt(), broadcastIntent3, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
                .setContentText("How long is the line at "+barName+"?")
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .addAction(R.drawable.ic_home_black_24dp,  spannableShort, actionIntent1)
                .addAction(R.drawable.ic_home_black_24dp, spannableMed, actionIntent2)
                .addAction(R.drawable.ic_home_black_24dp, spannableLong, actionIntent3);
        ;


        Notification myNotification = notifyBuilder.build();
        System.out.println("barName:"  + barName);
        if(!barNotifsSent.containsKey(barName)) {
            mNotifyManager.notify(NOTIFICATION_ID, myNotification);
            barNotifsSent.put(barName,barName);
        }
    }

    public void checkLocation(BottomNavigationView navView){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference barRef = mDatabase.child("bars/");

        barRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

        public void onComplete(@NonNull Task<DataSnapshot> task) {
                List<String> list = new ArrayList<>();
                for(DataSnapshot data: task.getResult().getChildren()){
                    String barName = data.getKey();
                    Double latitude = Double.parseDouble(data.child("latitude").getValue().toString());
                    Double longitude = Double.parseDouble(data.child("longitude").getValue().toString());;
                    System.out.println("Distance"+ distance(latitude, lat,longitude,lon, 0.0,0.0));
                    if(distance(latitude, lat,longitude,lon,0.0,0.0) < (double)200 ) {
                        sendNotification(navView, barName);
                    }
                }
            }



        });
    }

    //Code Taken From Stack Overflow: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) ;

        return Math.sqrt(distance);
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