package com.example.bouncermodule.ui.bars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.FragmentBarsBinding;
import com.example.bouncermodule.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;




public class BarsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FragmentBarsBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BarsViewModel barsViewModel =
                new ViewModelProvider(this).get(BarsViewModel.class);

        binding = FragmentBarsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment BarsFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
//        BarsFragment.getMapAsync(this);


        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    @Override
//    public boolean onMarkerClick(@NonNull Marker marker) {
//        return false;
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(43.0722, -89.4008);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

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