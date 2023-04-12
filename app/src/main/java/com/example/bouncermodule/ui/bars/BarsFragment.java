package com.example.bouncermodule.ui.bars;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class BarsFragment extends Fragment  {


    private FragmentBarsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BarsViewModel barsViewModel =
                new ViewModelProvider(this).get(BarsViewModel.class);

        binding = FragmentBarsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View contentView = inflater.inflate(R.layout.fragment_bars, container, false);
        ListView listView = contentView.findViewById(R.id.bar_list);
        // sample data
        List<String> list = new ArrayList<>();
        for(int i=0;i<100;i++)
            list.add("Item "+i);

        CustomAdapter listAdapter = new CustomAdapter(list);
        listView.setAdapter(listAdapter);


        System.out.println("left tab  ");
////        BarsFragment.getMapAsync(this);


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
            textView.setText(items.get(i));
            textView.setText("Bar Name:         Kollege Klub\nWait Time:         35 Minutes");

            return textView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}