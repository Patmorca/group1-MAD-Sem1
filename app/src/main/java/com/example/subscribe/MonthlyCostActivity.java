package com.example.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;

public class MonthlyCostActivity extends AppCompatActivity implements MainListRVInterface {
    RecyclerView recyclerView;
    FirebaseFirestore subDB;
    ArrayList<Subscription> Subarraylist;
    TextView Total;
    Monthly_Cost_Adapter myAdapter;
    SearchView searchView;
    Button sortBtn;
    int sortIndex;

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

        sortBtn = findViewById(R.id.AMC_SortBtn);
        sortIndex = 0;
        sortBtn.setText("Sort: $ \u2B07");

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortOptions();
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

        Total = findViewById(R.id.AMC_Price);

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
                getTotalCost(Subarraylist);
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

    private void sortOptions()
    {
        if(sortIndex == 3)
        {
            sortIndex = 0;
        }
        else
        {
            sortIndex++;
        }


        if(sortIndex == 0)
        {
            sortBtn.setText("Sort: $ \u2B07");
            //Sort option code
        }
        else if(sortIndex == 1)
        {
            sortBtn.setText("Sort: $ \u2B06");
            //Sort option code
        }
        else if (sortIndex == 2)
        {
            sortBtn.setText("Sort: A \u2B07");
            //Sort option code
        }
        else if (sortIndex == 3)
        {
            sortBtn.setText("Sort: A \u2B06");
            //Sort option code
        }

    }

    private void getTotalCost(ArrayList<Subscription> subscriptions) //Displays next month's total.
    {

        Log.d("tcost",String.valueOf(subscriptions.size()));

        float total = 0;
        Calendar calendar  = Calendar.getInstance();
        int nextMonth = calendar.get(Calendar.MONTH);
        nextMonth += 2;

        for(Subscription sub : subscriptions)
        {
            Calendar subDate = DueDate.dueDateAMC(sub);

            Log.d("tcost",String.valueOf(subDate.get(Calendar.MONTH)));
            Log.d("tcost",String.valueOf(nextMonth));

            if(nextMonth == (subDate.get(Calendar.MONTH) + 1))
            {
                total += sub.getCost();
            }
        }
        String totalOut = "$" + String.valueOf(total);
        Total.setText(totalOut);
    }


}