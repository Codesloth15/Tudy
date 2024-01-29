package com.example.tudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    TextView Login_TextView;
    TextInputEditText username,email,password,conFirmPassword;
    Button signup_Button;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializingVariables();
        onClick();
    }


    private void initializingVariables() {
        Login_TextView = findViewById(R.id.Login_TextView);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        conFirmPassword = findViewById(R.id.confirmpassword);
        signup_Button = findViewById(R.id.signup_Button);
        mDatabase = FirebaseDatabase.getInstance().getReference("newUser");
    }
    private void onClick() {
        Login_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
               finish();
            }
        });
        signup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
          String userName =  username.getText().toString().trim();
          String passWord =  password.getText().toString().trim();
          String emailData = email.getText().toString().trim();
       if(userName.isEmpty()){
           username.setError("Username cannot be empty");
           username.requestFocus();
           return;
       }
        if(passWord.isEmpty()){
            password.setError("Password cannot be empty");
            password.requestFocus();
            return;
        }
        if(emailData.isEmpty()){
            email.setError("Email cannot be empty");
            email.requestFocus();
            return;
        }

       firebaseAuth.createUserWithEmailAndPassword(emailData, passWord)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Set display name for the user
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // Send email verification
                            sendEmailVerification(user);

                            // Save user details to Realtime Database
                            saveUserDetailsToDatabase(user.getUid(), emailData, userName);

                            Toast.makeText(getApplicationContext(), "Registration successful. Please check your email for verification. "+email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    private void saveUserDetailsToDatabase(String uid,  String email, String username) {
        DatabaseReference userRef = mDatabase.child(uid);
        userRef.child("email").setValue(email);
        userRef.child("username").setValue(username);

    }
    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EmailVerification", "Email sent.");
                        } else {
                            Log.e("EmailVerification", "Failed to send verification email.", task.getException());
                        }
                    }
                });

    }

}