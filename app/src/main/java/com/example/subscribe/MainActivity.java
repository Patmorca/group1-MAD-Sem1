package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> subscriptions = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Button AddSub = null;
    FirebaseFirestore subDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AddSub = (Button) findViewById(R.id.AM_AddSubBtn);
        AddSub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, AddSubscriptionActivity.class);
                startActivity(intent);
            }
        });

        subDB = FirebaseFirestore.getInstance();
        ListView subList = (ListView) findViewById(R.id.AM_SubscriptionListView);
        adapter = new ArrayAdapter<String>(this,R.layout.mainviewlist, subscriptions);
        subList.setAdapter(adapter);


        subListCreate();



    }

    private void subListCreate()
    {
        FirebaseAuth tempAuth;
        tempAuth = FirebaseAuth.getInstance();
        FirebaseUser userGet = tempAuth.getCurrentUser();
        assert userGet != null;
        String userEmail = userGet.getEmail();


        subDB.collection("subscriptions").document(userEmail).collection("subscriptions")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) //For each document in the collection i.e for each sub in subscriptions
                        {

                            Map<String,Object> gotSub = document.getData(); //Retrieves Name + Subscription object from DB
                            for(String key : gotSub.keySet()) //Iterates through Map (Only contains 1 so it only iterates once)
                            {
                                adapter.add(key); //Adds key (Name);
                            }
                        }
                    }
                }
            });



    }

}