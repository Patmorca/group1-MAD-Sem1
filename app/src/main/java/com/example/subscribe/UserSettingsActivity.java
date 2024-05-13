package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.auth.User;

public class UserSettingsActivity extends AppCompatActivity {

    TextView user,email;
    EditText newPassword, confirmPassword, currentPassword;
    Button changeBtn, homeBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.USERSETTINGS), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        user = findViewById(R.id.AUS_User);
        email = findViewById(R.id.AUS_CurrentEmail);

        newPassword = findViewById(R.id.AUS_Password);
        confirmPassword = findViewById(R.id.AUS_ConfirmPassword);
        currentPassword = findViewById(R.id.AUS_CurrentPassword);

        changeBtn = findViewById(R.id.AUS_ChangeBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        String username = currentUser.getEmail().replaceAll("@.*","");
        user.setText(username);
        email.setText(currentUser.getEmail());

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passwordOut = newPassword.getText().toString().trim();
                String passwordCheck = confirmPassword.getText().toString().trim();
                String currentPasswordCheck = currentPassword.getText().toString().trim();

                if(passwordOut.isEmpty() || currentPasswordCheck.isEmpty() || passwordCheck.isEmpty())
                {
                    Log.d("reauth","FAILURE - empty strings");
                    Toast emptyError = Toast.makeText(UserSettingsActivity.this,"One or more fields are empty",Toast.LENGTH_LONG);
                    emptyError.show();
                }
                else{
                    if(passwordOut.equals(passwordCheck))
                    {

                        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(),currentPasswordCheck);

                        currentUser.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            currentUser.updatePassword(passwordOut).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Log.d("reauth","SUCCESS");
                                                        Toast updated = Toast.makeText(UserSettingsActivity.this,"Password Changed",Toast.LENGTH_LONG);
                                                        updated.show();
                                                        Intent intent = new Intent(UserSettingsActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                    else{
                                                        Log.d("reauth","FAILURE - Password not updated");
                                                        Toast passError = Toast.makeText(UserSettingsActivity.this,"Password does not meet security requirements",Toast.LENGTH_LONG);
                                                        passError.show();
                                                    }
                                                }
                                            });
                                        }
                                        else{
                                            Log.d("reauth","FAILURE - Reauth failure");
                                        }
                                    }
                                });


                    }
                    else
                    {
                        Toast notSame = Toast.makeText(UserSettingsActivity.this,"Passwords do not match",Toast.LENGTH_LONG);
                        notSame.show();
                    }
                }
            }
        });



        homeBtn = findViewById(R.id.AUS_HomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSettingsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }


}