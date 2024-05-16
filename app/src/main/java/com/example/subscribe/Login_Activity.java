package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Activity extends AppCompatActivity {
    Button Login , Register, Crash;
    EditText setEmail , setPass;
    ProgressBar progressBar;

    // Firebase Auth

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Login = findViewById(R.id.AL_Loginbt);
        Register = findViewById(R.id.AL_Registerbt);
        setEmail = findViewById(R.id.Al_EmaiL);
        setPass = findViewById(R.id.AL_Password);
        progressBar = findViewById(R.id.AL_progressBar);


        Register.setOnClickListener(v -> {
            Intent i = new Intent(Login_Activity.this, SignUp_Activity.class);
            startActivity(i);
        });

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        Login.setOnClickListener(v -> {
            login(
                    setEmail.getText().toString().trim(),
                    setPass.getText().toString().trim()
            );
        });


        Crash = findViewById(R.id.AL_CrashBtn);
        Crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("Crashlytics Test");
            }
        });

    }

    private void login(String email ,  String pass) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            progressBar.setVisibility(View.VISIBLE); // Show the progress bar
            firebaseAuth.signInWithEmailAndPassword(email , pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressBar.setVisibility(View.GONE);
                    Intent i = new Intent(Login_Activity.this , MainActivity.class);
                    startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login_Activity.this, "Wrong email or password" , Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Please enter email and password" , Toast.LENGTH_LONG).show();
        }
    }
}