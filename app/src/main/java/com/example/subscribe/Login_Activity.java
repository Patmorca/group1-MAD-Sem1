package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {
    Button Login , Register;
    EditText setEmail , setPass;

    // Firebase Auth

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Login = findViewById(R.id.AL_Loginbt);
        Register = findViewById(R.id.AL_Registerbt);
        setEmail = findViewById(R.id.Al_EmaiL);
        setPass = findViewById(R.id.AL_Password);

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
    }

    private void login(String email ,  String pass) {
        if(!TextUtils.isEmpty(email) && ! TextUtils.isEmpty(email)){
            firebaseAuth.signInWithEmailAndPassword(email , pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Intent i = new Intent(Login_Activity.this , MainActivity.class);
                    startActivity(i);
                }
            });
        }
    }
}