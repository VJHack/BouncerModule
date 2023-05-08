package com.example.bouncermodule.ui.dashboard;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bouncermodule.Bars;
import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.FragmentBarsBinding;
import com.example.bouncermodule.databinding.FragmentMapBinding;
import com.example.bouncermodule.ui.bars.BarsFragment;
import com.example.bouncermodule.ui.bars.BarsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;

    ArrayList<Object[]> bars;

    FusedLocationProviderClient mFusedLocationClient;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        bars = new ArrayList<Object[]>();
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SupportMapFragment DashboardFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);

        DashboardFragment.getMapAsync(this);


        SupportMapFragment BarsFragment = (SupportMapFragment) getParentFragmentManager()
                .findFragmentById(R.id.map);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference barRef = mDatabase.child("bars/");
        barRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String barName = data.getKey();
                    String length = data.child("lineLength").getValue().toString();
                    int capacity = Integer.parseInt(data.child("lineCount").getValue().toString());

                    System.out.println(length);
                    Double latitude = Double.parseDouble(data.child("latitude").getValue().toString());
                    Double longitude = Double.parseDouble(data.child("longitude").getValue().toString());;
                    Object[] tempBar = {longitude, latitude, barName, length, capacity};
                    bars.add(tempBar);
                }

                for(int i = 0; i < bars.size(); i++){

                    LatLng toAddBarMarker = new LatLng( Double.valueOf(bars.get(i)[0].toString()), Double.valueOf(bars.get(i)[1].toString()));
                    if(bars.get(i)[3].toString().equalsIgnoreCase("none")){
                        mMap.addMarker(new MarkerOptions()
                                .position(toAddBarMarker)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title(bars.get(i)[2].toString())
                                .snippet("Line: " + bars.get(i)[3] ));

                    }
                    else if(bars.get(i)[3].toString().equalsIgnoreCase("short")){
                        mMap.addMarker(new MarkerOptions()
                                .position(toAddBarMarker)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .title(bars.get(i)[2].toString())
                                .snippet("Line : " + bars.get(i)[3]));

                    }
                    else if(bars.get(i)[3].toString().equalsIgnoreCase("medium")){
                        mMap.addMarker(new MarkerOptions()
                                .position(toAddBarMarker)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .title(bars.get(i)[2].toString())
                                .snippet("Line: " + bars.get(i)[3] ));

                    }
                    else {
                        mMap.addMarker(new MarkerOptions()
                                .position(toAddBarMarker)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title(bars.get(i)[2].toString())
                                .snippet("Line: " + bars.get(i)[3]));
                    }
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

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
////                            latitudeTextView.setText(location.getLatitude() + "");
////                            longitTextView.setText(location.getLongitude() + "");
//                        }
//                    }
//                });
//            } else {
////                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            // if permissions aren't available,
//            // request for permissions
//            requestPermissions();
//        }
//    }
//    private LocationCallback mLocationCallback = new LocationCallback() {
//
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location mLastLocation = locationResult.getLastLocation();
////            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
////            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
//        }
//    };
//
//    // method to check for permissions
//    private boolean checkPermissions() {
//        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//        // If we want background location
//        // on Android 10.0 and higher,
//        // use:
//        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    // method to request for permissions
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this, new String[]{
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
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
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
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
//        // on FusedLocationClient
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(43.0722, -89.4008);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        for(int i = 0; i < bars.size(); i++){
            LatLng toAddBarMarker = new LatLng( Double.valueOf(bars.get(i)[0].toString()), Double.valueOf(bars.get(i)[1].toString()));
            mMap.addMarker(new MarkerOptions()
                    .position(toAddBarMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(bars.get(i)[2].toString()));
        }



//
//        for(int i = 0; i < bars.length; i++){
//            LatLng barLoc = new LatLng( (double) bars[i][0] ,  (double) bars[i][1]);
//            testMarker = mMap.addMarker(new MarkerOptions()
//                    .position(barLoc)
//                    .snippet("(608)-098-2647")
//
//                    .title(bars[i][2].toString()));
//            testMarker.setTag(0);
//        }
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                // on marker click we are getting the title of our marker
//                // which is clicked and displaying it in a toast message.
//                String markerName = marker.getTitle();
////                Toast.makeText(BarsFragment.this, "Clicked location is " + markerName, Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
    }
}