package com.example.bouncermodule.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.ActivityMainBinding;
import com.example.bouncermodule.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment  implements View.OnClickListener {
    private TextView counterValue;
    private TextView currentLength;
    private Button noneButton;
    private Button shortButton;
    private Button mediumButton;
    private Button longButton;

    private Button plusButton;
    private Button minusButton;
    private int counterValInt = 0;

    private ImageView photoIdImageView;
    private Button photoIdButton;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        View myView = inflater.inflate(R.layout.fragment_home, container, false);
        noneButton = (Button) myView.findViewById(R.id.None);
        noneButton.setOnClickListener(this);
        shortButton = (Button) myView.findViewById(R.id.Short);
        shortButton.setOnClickListener((View.OnClickListener) this);
        mediumButton = (Button) myView.findViewById(R.id.Medium);
        mediumButton.setOnClickListener((View.OnClickListener) this);
        longButton = (Button) myView.findViewById(R.id.Long);
        longButton.setOnClickListener((View.OnClickListener) this);

        currentLength = (TextView) myView.findViewById(R.id.CurrentLengthValue);
        counterValue = (TextView) myView.findViewById(R.id.Total_Value);
        plusButton = (Button) myView.findViewById(R.id.plus);
        plusButton.setOnClickListener((View.OnClickListener) this);
        minusButton = (Button) myView.findViewById(R.id.minus);
        minusButton.setOnClickListener((View.OnClickListener) this);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return myView;
    }

    public void onClick(View view) {
        if(view.getId() == R.id.Short) {
            Log.d("LOG", "NONE CLICKED");
            currentLength.setText("SHORT");
            currentLength.setTextColor(Color.parseColor("#0000FF"));
        }else if(view.getId() == R.id.Medium){
            Log.d("LOG", "MEDIUM CLICKED");
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}