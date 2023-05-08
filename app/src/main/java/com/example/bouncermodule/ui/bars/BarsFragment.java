package com.example.bouncermodule.ui.bars;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bouncermodule.Bars;
import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.FragmentBarsBinding;
import com.example.bouncermodule.databinding.FragmentHomeBinding;
import com.example.bouncermodule.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarsFragment extends Fragment  {


    private FragmentBarsBinding binding;
    private DatabaseReference mDatabase;
    private Map<String, Bars> barsMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BarsViewModel barsViewModel =
                new ViewModelProvider(this).get(BarsViewModel.class);

        binding = FragmentBarsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View contentView = inflater.inflate(R.layout.fragment_bars, container, false);
        ListView listView = contentView.findViewById(R.id.bar_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference barRef = mDatabase.child("bars/");
        barRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                List<String> list = new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String barName = data.getKey();
                    String lineLength = data.child("lineLength").getValue().toString();
                    Integer lineCount = Integer.parseInt(data.child("lineCount").getValue().toString());
                    Double latitude = Double.parseDouble(data.child("latitude").getValue().toString());
                    Double longitude = Double.parseDouble(data.child("longitude").getValue().toString());;

                    Bars tempBar = new Bars(lineLength, lineCount, longitude, latitude);
                    barsMap.put(barName, tempBar);
                    list.add(barName);
                }
                CustomAdapter listAdapter = new CustomAdapter(list);
                listView.setAdapter(listAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

//        final TextView textView = binding.textBars;
//        barsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        barsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        SupportMapFragment BarsFragment = (SupportMapFragment) getParentFragmentManager()
                .findFragmentById(R.id.map);
        System.out.println("Barrrrrrrrrrrrr  " + BarsFragment);
        //// BarsFragment.getMapAsync(this);



        return contentView;
    }

    class CustomAdapter extends BaseAdapter {
        List<String> items;

        public CustomAdapter(List<String> items) {
            super();
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).hashCode();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(getContext());
            String barName = items.get(i);
            textView.setText(barName);
            String lineLength = barsMap.get(barName).getLineLength();
            // Capitalize first letter then make rest of string lower case
            lineLength = lineLength.substring(0, 1).toUpperCase() + lineLength.substring(1).toLowerCase();
            int lineCount = barsMap.get(barName).getLineCount();
            textView.setText("Bar Name:         " + barName + "\nLine Length:      " + lineLength + "\nCurrent Capacity: " + lineCount);

            return textView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}