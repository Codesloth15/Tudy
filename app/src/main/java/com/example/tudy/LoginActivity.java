package com.example.tudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Handler;


import java.util.stream.Stream;

public class LoginActivity extends AppCompatActivity {
 private    Button SignUp_Button,Login_Button;
 private    TextView clickhere_TextView;
  private   TextInputEditText email,password;

    private DatabaseReference mDatabase;
    ProgressBar loading;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializingVariables();
        onClick();


    }


    public void initializingVariables(){
        SignUp_Button = findViewById(R.id.SignUp_Button);
        clickhere_TextView = findViewById(R.id.clickhere_TextView);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Login_Button = findViewById(R.id.Login_Button);
        loading = findViewById(R.id.loading);

    }
    private void onClick() {
        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        SignUp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        clickhere_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
    protected void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // This code will be executed after a 2-second delay
                loading.setVisibility(View.GONE);
            }
        }, 2000);

        FirebaseUser  user = firebaseAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            String currentUser = user.getUid();
            DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("newUser").child(currentUser);

            ValueEventListener userEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            studentRef.addListenerForSingleValueEvent(userEventListener);
        }else {
            loading.setVisibility(View.GONE);
        }

    }
    private void login(){
        String emailData =  email.getText().toString().trim();
        String passwordData = password.getText().toString().trim();

        if (emailData.isEmpty()) {
            // Display a warning for empty email field
            email.setError("Email cannot be empty");
            email.requestFocus();
            return;  // Return to avoid further processing if email is empty
        }

        if (passwordData.isEmpty()) {
            // Display a warning for empty password field
            password.setError("Password cannot be empty");
            password.requestFocus();
            return;  // Return to avoid further processing if password is empty
        }
        firebaseAuth.signInWithEmailAndPassword(emailData, passwordData).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Please verify your email before logging in ", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}