package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class MonthlyCostActivity extends AppCompatActivity implements MainListRVInterface {
    RecyclerView recyclerView;
    FirebaseFirestore subDB;
    ArrayList<Subscription> Subarraylist;

    Monthly_Cost_Adapter myAdapter;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_cost);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MONTHLYCOST), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchView = findViewById(R.id.AMC_viewSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        subDB = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.AMC_RecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Subarraylist = new ArrayList<Subscription>();
        myAdapter = new Monthly_Cost_Adapter(MonthlyCostActivity.this, Subarraylist , this);
        recyclerView.setAdapter(myAdapter);
        Eventchangelistener();
    }

    private void filterList(String newText) {
        ArrayList<Subscription> filteredList = new ArrayList<>();
        for(Subscription subs : Subarraylist ){
            if(subs.getSubName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(subs);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this , "No data found" , Toast.LENGTH_LONG).show();
        }else {
            myAdapter.setFileterdList(filteredList);
        }
    }

    private void Eventchangelistener() {
        FirebaseAuth tempAuth;
        tempAuth = FirebaseAuth.getInstance();
        FirebaseUser userGet = tempAuth.getCurrentUser();
        assert userGet != null;
        String userEmail = userGet.getEmail();
        subDB.collection("subscriptions").document(userEmail).collection("subscriptions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Error" ,error.getMessage());
                    return;
                }
                Subarraylist.clear();
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED) {
                        Subarraylist.add(dc.getDocument().toObject(Subscription.class));
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override // View Sub
    public void onItemClick(int pos) {
        Intent intent = new Intent(MonthlyCostActivity.this, ViewSubscriptionActivity.class);
        intent.putExtra("Subscription", new Gson().toJson(Subarraylist.get(pos)));
        startActivity(intent);
    }

}