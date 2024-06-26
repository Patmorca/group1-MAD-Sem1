package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Setting extends AppCompatActivity {

    TextView user,email;
    EditText newPassword, currentPassword;
    Button changeBtn, homeBtn,addSubBtn;
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SETTING), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.AS_ProgressBar); // Initialise progress bar

        homeBtn = findViewById(R.id.AS_HomeBtn); //Bind button for main activity
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this,MainActivity.class);
                startActivity(intent);
            }
        });

        addSubBtn = findViewById(R.id.AS_AddSubBtn); // Bind button for add subscription activity.
        addSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this,AddSubscriptionActivity.class);
                startActivity(intent);
            }
        });


        // Initialise and bind

        user = findViewById(R.id.AS_User);
        email = findViewById(R.id.AS_Email);

        newPassword = findViewById(R.id.AS_NewPassword);
        currentPassword = findViewById(R.id.AS_CurrentPassword);

        changeBtn = findViewById(R.id.AS_SaveBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        assert currentUser != null;
        String username = currentUser.getEmail().replaceAll("@.*",""); // Creates a "pseudo" username to display
        user.setText(username);
        email.setText(currentUser.getEmail()); // sets email

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passwordOut = newPassword.getText().toString().trim();
                    String currentPasswordCheck = currentPassword.getText().toString().trim();

                if(passwordOut.isEmpty() || currentPasswordCheck.isEmpty()) // Checks that neither field is empty
                {
                    Log.d("reauth","FAILURE - empty strings");
                    Toast emptyError = Toast.makeText(Setting.this,"One or more fields are empty",Toast.LENGTH_LONG);
                    emptyError.show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE); // Progress bar visibility

                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(),currentPasswordCheck); // Passes current password check to auth credential

                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updatePassword(passwordOut).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()){
                                                    Log.d("reauth","SUCCESS");
                                                    Toast updated = Toast.makeText(Setting.this,"Password Changed",Toast.LENGTH_LONG);
                                                    updated.show();
                                                    Intent intent = new Intent(Setting.this, MainActivity.class); // Returns to main
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Log.d("reauth","FAILURE - Password not updated");
                                                    Toast passError = Toast.makeText(Setting.this,"Password does not meet security requirements",Toast.LENGTH_LONG);
                                                    passError.show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        Log.d("reauth","FAILURE - Reauth failure");
                                        Toast.makeText(Setting.this, "Wrong current password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
    }
}