package com.example.subscribe;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements MainListRVInterface {

    Button AddSub = null;
    Button settingsBtn;
    Button logoutBtn;
    FirebaseFirestore subDB;
    private static final int RC_NOTIFICATION = 99;
    TextView Total;
    RecyclerView recyclerView;
    ArrayList<Subscription> Subarraylist;
    SubAdapter subAdapter;
    TextView AM_price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS},RC_NOTIFICATION);}
            return insets;
        });



        AM_price = findViewById(R.id.AM_Price);
        AM_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MonthlyCostActivity.class);
                startActivity(i);
            }
        });
        AddSub = (Button) findViewById(R.id.AM_AddSubBtn);
        AddSub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, AddSubscriptionActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn = findViewById(R.id.AM_SettingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
            }
        });

        logoutBtn = findViewById(R.id.AM_LogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,Login_Activity.class);
                startActivity(intent);
            }
        });

        subDB = FirebaseFirestore.getInstance();

        Total = findViewById(R.id.AM_Price);

        recyclerView = findViewById(R.id.MA_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Subarraylist = new ArrayList<Subscription>();
        subAdapter = new SubAdapter(MainActivity.this, Subarraylist,this );
        recyclerView.setAdapter(subAdapter);
        Eventchangelistener();


    }

    private void Eventchangelistener() {
        FirebaseAuth tempAuth;
        tempAuth = FirebaseAuth.getInstance();
        FirebaseUser userGet = tempAuth.getCurrentUser();
        assert userGet != null;
        String userEmail = userGet.getEmail();
        assert userEmail != null;
        subDB.collection("subscriptions").document(userEmail).collection("subscriptions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Error" ,error.getMessage());
                    return;
                }
                Subarraylist.clear();
                assert value != null;
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED) {
                        Subarraylist.add(dc.getDocument().toObject(Subscription.class));
                    }
                }
                getTotalCost(Subarraylist);
                subAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override // View Sub
    public void onItemClick(int pos) {
        Intent intent = new Intent(MainActivity.this, ViewSubscriptionActivity.class);
        intent.putExtra("Subscription", new Gson().toJson(Subarraylist.get(pos)));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RC_NOTIFICATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"ALLOWED",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"DENIED",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getTotalCost(ArrayList<Subscription> subscriptions) //Displays next month's total.
    {

        Log.d("tcost",String.valueOf(subscriptions.size()));

        float total = 0;
        Calendar calendar  = Calendar.getInstance();
        int nextMonth = calendar.get(Calendar.MONTH);
//        nextMonth += 2;

        for(Subscription sub : subscriptions)
        {
            Calendar subDate = DueDate.dueDateAMC(sub);

            Log.d("tcost",String.valueOf(subDate.get(Calendar.MONTH)));
            Log.d("tcost",String.valueOf(nextMonth));

            if(nextMonth == (subDate.get(Calendar.MONTH)))
            {
                total += sub.getCost();
            }
        }
        String totalOut = "$" + total;
        Total.setText(totalOut);
    }



}