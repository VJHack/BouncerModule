package com.example.bouncermodule.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.FragmentBarsBinding;
import com.example.bouncermodule.databinding.FragmentMapBinding;
import com.example.bouncermodule.ui.bars.BarsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;

    private GoogleMap mMap;

    Object[][] bars = {
            {43.07247820000001, -89.38480299999999, "Paradise Lounge"},
            {43.0753202, -89.39044, "The Plaza Tavern"},
            {43.0740484, -89.3932195, "Chasers 2.0"}
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SupportMapFragment DashboardFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        System.out.println("Barrrrrrrrrrrrr  " + DashboardFragment);
        DashboardFragment.getMapAsync(this);



        SupportMapFragment BarsFragment = (SupportMapFragment) getParentFragmentManager()
                .findFragmentById(R.id.map);
        System.out.println("Barrrrrrrrrrrrr  " + BarsFragment);
//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

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

        for(int i = 0; i < bars.length; i++){


            LatLng toAddBarMarker = new LatLng( Double.valueOf(bars[i][0].toString()), Double.valueOf(bars[i][1].toString()));
            mMap.addMarker(new MarkerOptions()
                    .position(toAddBarMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(bars[i][2].toString()));
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