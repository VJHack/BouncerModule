package com.example.bouncermodule.ui.authentication;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bouncermodule.MainActivity;
import com.example.bouncermodule.R;
//import com.example.bouncermodule.databinding.FragmentAuthenticationBinding;
import com.example.bouncermodule.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthenticationFragment extends AppCompatActivity {

    //private EditText email;
    //private EditText password;
    private static final String TAG = "Authentication";
    private FirebaseAuth mAuth;
    //private FragmentAuthenticationBinding mBinding;
    private Button login;
    private EditText email;
    private EditText password;
    private Button createAccount;
    private TextView status;
    private String userEmail;
    //private FirebaseUser user;

//    public View onCreate(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//      //  mBinding = inflater.inflate(R.layout.fragment_authentication, container, false);
////        email = (EditText) contentView.findViewById(R.id.email);
////        password = (EditText) contentView.findViewById(R.id.password);
//        mBinding = FragmentAuthenticationBinding.inflate(inflater, container, false);
//        return mBinding.getRoot();
//
//        //return contentView;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_authentication);



        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        createAccount = findViewById(R.id.createAccount);
        status = findViewById(R.id.status);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                signIn(email, password);
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                createAccount(email, password);
            }
        });
//        mBinding.signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });
//        mBinding.verifyEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendEmailVerification();
//            }
//        });
//        mBinding.reloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reload();
//            }
//        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String bar = NULL;
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String error = task.getException().toString();
                            String printedError = error.substring(error.lastIndexOf(":") + 2, error.length() - 1);
                            Toast.makeText(getApplicationContext(), printedError,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userEmail = user.getEmail();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            String error = task.getException().toString();
                            String printedError = error.substring(error.lastIndexOf(":") + 2, error.length() - 1);
                            Toast.makeText(getApplicationContext(), printedError,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            status.setText("Login Failed");
                        }
                    }
                });
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(getApplicationContext(),
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "reload", task.getException());
                    Toast.makeText(getApplicationContext(),
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getEmail() {
        return userEmail;
    }

    private boolean validateForm() {
        boolean valid = true;
        EditText email = findViewById(R.id.email);
        String emailString = email.getText().toString();
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        EditText password = findViewById(R.id.password);
        String passwordString = password.getText().toString();
        if (TextUtils.isEmpty(passwordString)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(AuthenticationFragment.this, MainActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();

        } else {
            //stay on page

        }
    }





}
