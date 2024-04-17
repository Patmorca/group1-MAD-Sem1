package com.example.subscribe;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddSubscriptionActivity extends AppCompatActivity {

    private Spinner nameSpinner;

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


        nameSpinner = findViewById(R.id.NameOptions);
        String[] subscriptions = getResources().getStringArray(R.array.SubscriptionNames);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,subscriptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);

    }
}