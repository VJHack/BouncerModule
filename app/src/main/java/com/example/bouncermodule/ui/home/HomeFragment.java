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

import com.example.bouncermodule.Bars;
import com.example.bouncermodule.R;
import com.example.bouncermodule.databinding.ActivityMainBinding;
import com.example.bouncermodule.databinding.FragmentHomeBinding;
import com.example.bouncermodule.ui.authentication.AuthenticationFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment  implements View.OnClickListener {
    private TextView counterValue;
    private TextView currentLength;
    private Button noneButton;
    private Button shortButton;
    private Button mediumButton;
    private Button longButton;

    private Button plusButton;
    private Button minusButton;
    private int counterValInt;

    private ImageView photoIdImageView;
    private Button photoIdButton;
    private FragmentHomeBinding binding;
    // Firebase variables
    private DatabaseReference mDatabase;
    private Map<String, Bars> barsMap = new HashMap<>();

    private String associatedBar;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference barRef = mDatabase.child("bars/");
        barRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String barName = data.getKey();
                    String lineLength = data.child("lineLength").getValue().toString();
                    Integer lineCount = Integer.parseInt(data.child("lineCount").getValue().toString());
                    Double latitude = Double.parseDouble(data.child("latitude").getValue().toString());
                    Double longitude = Double.parseDouble(data.child("longitude").getValue().toString());;
                    Bars tempBar = new Bars(lineLength, lineCount, longitude, latitude);
                    barsMap.put(barName, tempBar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        DatabaseReference userRef = mDatabase.child("VerifiedUsers/");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String userEmail = parseEmail(AuthenticationFragment.getEmail());
                    associatedBar = snapshot.child(userEmail).getValue().toString();
                    Bars tempBar = barsMap.get(associatedBar);
                    counterValInt = tempBar.getLineCount();
                    counterValue.setText("Total:    " +String.valueOf(counterValInt));
                    currentLength.setText(tempBar.getLineLength());
                    lineLengthColor();
                }catch(Exception e) {
                    // If in here the user is a regular user and not a bouncer
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    public void onClick(View view) {
        if(view.getId() == R.id.Short) {
            Log.d("LOG", "NONE CLICKED");
            currentLength.setText("SHORT");
            lineLengthColor();
        }else if(view.getId() == R.id.Medium){
            Log.d("LOG", "MEDIUM CLICKED");
            currentLength.setText("MEDIUM");
            lineLengthColor();
        }else if(view.getId() == R.id.Long){
            currentLength.setText("LONG");
            lineLengthColor();
        }else if(view.getId() == R.id.None){
            currentLength.setText("NONE");
            lineLengthColor();
        }else if(view.getId() == R.id.plus){
            counterValInt++;
            counterValue.setText("Total:    " +String.valueOf(counterValInt));
        }else if(view.getId() == R.id.minus){
            if(counterValInt >= 1) {
                counterValInt--;
            }
            counterValue.setText("Total:    " + String.valueOf(counterValInt));
        }

        // Changing the line count and line length of Mondays
        Bars tempBar = barsMap.get(associatedBar);
        tempBar.setLineCount(counterValInt);
        tempBar.setLineLength((String) currentLength.getText());
        mDatabase.child("bars").child(associatedBar).setValue(tempBar);
    }

    public void lineLengthColor() {
        switch (currentLength.getText().toString()) {
            case("LONG"):
                currentLength.setTextColor(Color.parseColor("#FF0000")); // Red
                break;
            case("MEDIUM"):
                currentLength.setTextColor(Color.parseColor("#FFA500")); // Yellow
                break;
            case("SHORT"):
                currentLength.setTextColor(Color.parseColor("#0000FF")); // Blue
                break;
            default:
                currentLength.setTextColor(Color.parseColor("#028A0F")); // Green
        }


    }

    private String parseEmail(String username) {
        return username.replaceAll("[.]", "_");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}