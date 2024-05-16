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
import java.util.Collections;

public class MonthlyCostActivity extends AppCompatActivity implements MainListRVInterface {
    RecyclerView recyclerView;
    FirebaseFirestore subDB;
    ArrayList<Subscription> Subarraylist;
    TextView Total;
    Monthly_Cost_Adapter myAdapter;
    SearchView searchView;
    Button sortBtn;
    Button addSubBtn;
    Button homeBtn;
    Button settingsBtn;

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

        addSubBtn = findViewById(R.id.AMC_AddSubBtn); // Button bind for addsub activity
        addSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MonthlyCostActivity.this, AddSubscriptionActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn = findViewById(R.id.AMC_SettingsBtn); // Button bind for settings activity
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MonthlyCostActivity.this,Setting.class);
                startActivity(intent);
            }
        });

        homeBtn = findViewById(R.id.AMC_HomeBtn); // Button bind for home (Main view).
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MonthlyCostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        subDB = FirebaseFirestore.getInstance();

        // Recycler view initialisation

        recyclerView = findViewById(R.id.AMC_RecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Subarraylist = new ArrayList<Subscription>();
        myAdapter = new Monthly_Cost_Adapter(MonthlyCostActivity.this, Subarraylist , this);
        recyclerView.setAdapter(myAdapter);
        Eventchangelistener();

        Total = findViewById(R.id.AMC_Price);

    }


    //List filtering via search function

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

    //Recyclerview population logic

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
                sortList(0);
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
        if(sortIndex == 3) // Resets sort button when clicking out of range of options
        {
            sortIndex = 0;
        }
        else
        {
            sortIndex++;
        }


        if(sortIndex == 0)
        {
            sortBtn.setText("Sort: $ \u2B07"); // Unicode for arrows
            sortList(0);
        }
        else if(sortIndex == 1)
        {
            sortBtn.setText("Sort: $ \u2B06");
            sortList(1);
        }
        else if (sortIndex == 2)
        {
            sortBtn.setText("Sort: A \u2B07");
            sortList(2);
        }
        else if (sortIndex == 3)
        {
            sortBtn.setText("Sort: A \u2B06");
            sortList(3);
        }

    }

    private void sortList(int option) {

        ArrayList<String> sortedStringList = new ArrayList<>();
        ArrayList<Float> sortedCostList = new ArrayList<>();

        ArrayList<String> sortedSubs = new ArrayList<>();

        for(Subscription sub : Subarraylist)
        {
            sortedStringList.add(sub.getSubName());
            sortedCostList.add(sub.getCost());
        }

        ArrayList<Subscription> sortedSubList = new ArrayList<>(sortedStringList.size());


        // Depending on the current option i.e current state of the sort button, different sorts will be applied utilising the Collections.sort() method. The names/prices get sorted initially, and then
        // Based on the order of the sorted names/prices, their associated subscriptions are then sorted and passed back into the recycler view

        if(option == 0)
        {
            Collections.sort(sortedCostList);
            for(int i = 0; i < sortedCostList.size(); ++i)
            {
                float sortedCost = sortedCostList.get(i);
                for(int j = 0; j < Subarraylist.size(); ++j)
                {
                    String subName = Subarraylist.get(j).getSubName();
                    float subCost = Subarraylist.get(j).getCost();

                    if(sortedCost == subCost && !sortedSubs.contains(subName)) {sortedSubList.add(Subarraylist.get(j));sortedSubs.add(subName);}
                }
            }
            myAdapter.setSortedList(sortedSubList);
        }
        else if(option == 1)
        {
            Collections.sort(sortedCostList);
            Collections.reverse(sortedCostList);
            for(int i = 0; i < sortedCostList.size(); ++i)
            {
                float sortedCost = sortedCostList.get(i);
                for(int j = 0; j < Subarraylist.size(); ++j)
                {
                    String subName = Subarraylist.get(j).getSubName();
                    float subCost = Subarraylist.get(j).getCost();

                    if(sortedCost == subCost && !sortedSubs.contains(subName)) {sortedSubList.add(Subarraylist.get(j));sortedSubs.add(subName);} // Avoid duplicate prices being added to the list multiple times once they've already been sorted
                }
            }
            myAdapter.setSortedList(sortedSubList);
        }
        else if(option == 2)
        {
            Collections.sort(sortedStringList);
            for(int i = 0; i < sortedStringList.size(); ++i)
            {
                String sortedStringName = sortedStringList.get(i).trim();
                for(int j = 0; j < Subarraylist.size(); ++j)
                {
                    String subName = Subarraylist.get(j).getSubName().trim();
                    if(subName.equals(sortedStringName)) {sortedSubList.add(Subarraylist.get(j));}
                }
            }
            myAdapter.setSortedList(sortedSubList);
        }
        else if(option == 3)
        {
            Collections.sort(sortedStringList);
            Collections.reverse(sortedStringList);
            for(int i = 0; i < sortedStringList.size(); ++i)
            {
                String sortedStringName = sortedStringList.get(i).trim();
                for(int j = 0; j < Subarraylist.size(); ++j)
                {
                    String subName = Subarraylist.get(j).getSubName().trim();
                    if(subName.equals(sortedStringName)) {sortedSubList.add(Subarraylist.get(j));}
                }
            }
            myAdapter.setSortedList(sortedSubList);
        }
    }

    private void getTotalCost(ArrayList<Subscription> subscriptions) //Displays this month's total.
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