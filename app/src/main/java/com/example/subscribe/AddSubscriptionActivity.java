package com.example.subscribe;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddSubscriptionActivity extends AppCompatActivity {

    Spinner freqSpinner;
    Spinner remSpinner;
    Button datePicker;
    Button Home;
    Button addSub;
    FirebaseFirestore subDB;
    FirebaseAuth tempAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ADDSUBSCRIPTION), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Home = findViewById(R.id.AAS_HomeBtn);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSubscriptionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addSub = findViewById(R.id.AAS_CreateBtn);
        addSub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    addSub();
                    Intent intent = new Intent(AddSubscriptionActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Log.d("Button","FAILURE");
                    return;
                };
            }
        });



        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        String datePickerInitial = day + "/" + month + "/" + year;
        datePicker = (Button) findViewById(R.id.AAS_DatePickerBtn);
        datePicker.setText(datePickerInitial);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(year,month,day);
            }
        });


        freqSpinner = findViewById(R.id.AAS_FreqOptions);
        String[] frequencies = getResources().getStringArray(R.array.FrequencyOptions);
        ArrayAdapter freqAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,frequencies);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(freqAdapter);

        remSpinner = findViewById(R.id.AAS_ReminderOptions);
        String[] reminders = getResources().getStringArray(R.array.ReminderOptions);
        ArrayAdapter remAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,reminders);
        remAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remSpinner.setAdapter(remAdapter);

    }

    private void openDialog(int year, int month, int day)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = (day)+"/"+(month)+"/"+(year);
                datePicker.setText(date);
            }
        }, year,month,day);
        dialog.show();
    }

    private void addSub() throws Exception
    {
        subDB = FirebaseFirestore.getInstance();
        tempAuth = FirebaseAuth.getInstance();
        FirebaseUser userGet = tempAuth.getCurrentUser();
        assert userGet != null;
        String userEmail = userGet.getEmail();

        EditText name = findViewById(R.id.AAS_Name);
        String nameOut = name.getText().toString();

        Spinner frequency = findViewById(R.id.AAS_FreqOptions);
        String frequencyOut = frequency.getSelectedItem().toString();

        Button startDate = findViewById(R.id.AAS_DatePickerBtn);
        String startDateString = startDate.getText().toString();
        @SuppressLint("SimpleDateFormat") Date startDateOut = new SimpleDateFormat("dd/MM/yyyy").parse(startDateString);

        Spinner reminder = findViewById(R.id.AAS_ReminderOptions);
        String reminderOut = reminder.getSelectedItem().toString();

        EditText cost = findViewById(R.id.AAS_Cost);
        float costOut = Float.parseFloat(cost.getText().toString());

        EditText email = findViewById(R.id.AAS_Email);
        String emailOut = email.getText().toString();

        EditText password = findViewById(R.id.AAS_Password);
        String passwordOut = password.getText().toString();

        Subscription subOut = new Subscription(nameOut,frequencyOut,startDateOut,reminderOut,costOut,emailOut,passwordOut);
        Map<String,Subscription> subStore = new HashMap<>();
        subStore.put(nameOut,subOut);




        subDB.collection("subscriptions").document(userEmail).collection("subscriptions").add(subStore);

        //Should definitely put some error checking in here but we can get around to that later.
        //

    }


}