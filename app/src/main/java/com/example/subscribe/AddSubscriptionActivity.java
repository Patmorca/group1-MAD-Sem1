package com.example.subscribe;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddSubscriptionActivity extends AppCompatActivity {

    private Spinner freqSpinner;
    private Spinner remSpinner;
    Button datePicker;
    Button Home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Home = (Button) findViewById(R.id.HomeBtn);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSubscriptionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        String datePickerInitial = day + "/" + month + "/" + year;
        datePicker = (Button) findViewById(R.id.DatePickerButton);
        datePicker.setText(datePickerInitial);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(year,month,day);
            }
        });


        freqSpinner = findViewById(R.id.FreqOptions);
        String[] frequencies = getResources().getStringArray(R.array.FrequencyOptions);
        ArrayAdapter freqAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,frequencies);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(freqAdapter);

        remSpinner = findViewById(R.id.ReminderOptions);
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


}