package com.example.bouncermodule;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bouncermodule.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    // Firebase variables
    private DatabaseReference mDatabase;
    private Map<String, Bars> barsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
//        noneButton = (Button) findViewById(R.id.None);
//        noneButton.setOnClickListener(this);
//        shortButton = (Button) findViewById(R.id.Short);
//        shortButton.setOnClickListener(this);
//        mediumButton = (Button) findViewById(R.id.Medium);
//        mediumButton.setOnClickListener(this);
//        longButton = (Button) findViewById(R.id.Long);
//        longButton.setOnClickListener(this);
//
//        currentLength = (TextView) findViewById(R.id.CurrentLengthValue);
//        counterValue = (TextView) findViewById(R.id.Total_Value);
//        plusButton = (Button) findViewById(R.id.plus);
//        plusButton.setOnClickListener(this);
//        minusButton = (Button) findViewById(R.id.minus);
//        minusButton.setOnClickListener(this);

        currentLength = (TextView) findViewById(R.id.CurrentLengthValue);
        counterValue = (TextView) findViewById(R.id.Total_Value);
        plusButton = (Button) findViewById(R.id.plus);
        plusButton.setOnClickListener(this);
        minusButton = (Button) findViewById(R.id.minus);
        minusButton.setOnClickListener(this);

        photoIdImageView = findViewById(R.id.imageviewphotoid);
        photoIdButton = findViewById(R.id.capturePhotoId);

        //Request for camera runtime permission
//        if (ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                    Manifest.permission.CAMERA
//            },100);
//        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_facerecognition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

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

    }

    // used for reading in file filled with new bars
    public void writeNewBar(String barId, String lineLength, Integer lineCount, Double longitude, Double latitude) {
        if (barsMap.containsKey(barId) == false){
            Bars bar = new Bars(lineLength, lineCount, longitude, latitude);
            Log.d("Writing new Bar", barId);
            barsMap.put(barId, bar);
            mDatabase.child("bars").child(barId).setValue(bar);
        }
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
        }else if(view.getId() == R.id.imageviewphotoid){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 100);
        }

        // Changing the line count and line length of Mondays
        Bars tempBar = barsMap.get("Mondays");
        tempBar.setLineCount(counterValInt);
        tempBar.setLineLength((String) currentLength.getText());
        mDatabase.child("bars").child("Mondays").setValue(tempBar);
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