package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp_Activity extends AppCompatActivity {
    EditText setName, setEmail , setPass;
    Button Registerbt, Backbt;

    // Firebase Auth

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseUser currentUser;
    // Firebase connection

    //WE CAN'T ALLOW DUPLICATE ACCOUNTS!

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Binding

        setName = findViewById(R.id.SUA_Name);
        setEmail = findViewById(R.id.SUA_Email);
        setPass = findViewById(R.id.SUA_PassW);
        Registerbt = findViewById(R.id.SUA_Registerbt);
        Backbt = findViewById(R.id.SUA_Backbt);


        Backbt.setOnClickListener(v -> {
            Intent i = new Intent(SignUp_Activity.this, Login_Activity.class);
            startActivity(i);
        });
        // firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){
                    // already log in
                } else{
                    // sign out
                }
            }
        };

        Registerbt.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(setName.getText().toString()) && !TextUtils.isEmpty(setEmail.getText().toString()) && !TextUtils.isEmpty(setPass.getText().toString())){
                String email = setEmail.getText().toString().trim();
                String name = setName.getText().toString().trim();
                String pass = setPass.getText().toString().trim();

                CreateUserEmailAcc(name,  email , pass);
            }else{
                Toast.makeText(SignUp_Activity.this,
                        "Missing Fields",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void CreateUserEmailAcc(String name, String email, String pass) {
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) ){
            firebaseAuth.createUserWithEmailAndPassword(
                    email, pass
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // Account Successfull

                        Toast.makeText(SignUp_Activity.this , "Registration Successful" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUp_Activity.this,Login_Activity.class);
                        startActivity(intent);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp_Activity.this,e.getMessage(),Toast.LENGTH_LONG).show(); // Duplicate account preventions
                }
            });
        }
    }
}