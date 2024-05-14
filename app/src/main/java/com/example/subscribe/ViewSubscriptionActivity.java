package com.example.subscribe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewSubscriptionActivity extends AppCompatActivity {

    TextView subName;
    TextView subCost;
    TextView subFreq;
    TextView startDate;
    TextView nextRem;
    TextView dueDate;
    Button homeBtn;
    Button viewLogin;
    Button deleteTracker;
    Button editSub;

    FirebaseFirestore subDb;
    FirebaseAuth tempAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.VIEWSUBSCRIPTION), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        subName = findViewById(R.id.AVS_Subname);
        subCost = findViewById(R.id.AVS_Cost);
        subFreq = findViewById(R.id.AVS_Frequency);
        startDate = findViewById(R.id.AVS_StartDate);
        nextRem = findViewById(R.id.AVS_Reminder);
        dueDate = findViewById(R.id.AVS_DueDate);
        deleteTracker = findViewById(R.id.AVS_Delete);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");



        Subscription subToView = new Gson().fromJson(getIntent().getStringExtra("Subscription"),Subscription.class);
        subName.setText(subToView.getSubName());
        subCost.setText(Double.toString(subToView.getCost()));
        subFreq.setText(subToView.getFrequency());
        startDate.setText(df.format(subToView.getStartDate()));
        nextRem.setText(calcNextRem(subToView));

        homeBtn = findViewById(R.id.AVS_HomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewSubscriptionActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        viewLogin = findViewById(R.id.AVS_ViewDetails);
        viewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLoginDetails();
            }
        });
        deleteTracker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Subscription subToView = new Gson().fromJson(getIntent().getStringExtra("Subscription"),Subscription.class);
                String SubNametoDelete =  subToView.getSubName();
                removeSubscription(SubNametoDelete);
            }
        });

    }

    String calcNextRem(Subscription subToView)
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date startDateCalc;
        String returnDate;
        String nextRem = subToView.getReminder();

        try {
            startDateCalc = df.parse(startDate.getText().toString());
        }
        catch (ParseException e)
        {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDateCalc);

        if(subFreq.getText().toString().equals("30 Days")) // Yes this is an absurdly long if else chain. No, I don't care.
        {
            c.add(Calendar.DATE,30);
        }
        else if(subFreq.getText().toString().equals("60 Days"))
        {
            c.add(Calendar.DATE,60);
        }
        else if(subFreq.getText().toString().equals("90 Days"))
        {
            c.add(Calendar.DATE,90);
        }
        else if(subFreq.getText().toString().equals("180 Days"))
        {
            c.add(Calendar.DATE,180);
        }
        else if(subFreq.getText().toString().equals("1 Month"))
        {
            c.add(Calendar.MONTH,1);
        }
        else if(subFreq.getText().toString().equals("3 Months"))
        {
            c.add(Calendar.MONTH,3);
        }
        else if(subFreq.getText().toString().equals("6 Months"))
        {
            c.add(Calendar.MONTH,6);
        }
        else if(subFreq.getText().toString().equals("12 Months"))
        {
            c.add(Calendar.MONTH,12);
        }

        dueDate.setText(df.format(c.getTime())); //This should not be couple to the nextRem setting but whatever.

        if(nextRem.equals("In 15 seconds"))
        {
            c.add(Calendar.DATE,0);
        }
        else if(nextRem.equals("The Day Before"))
        {
            c.add(Calendar.DATE,-1);
        }
        else if(nextRem.equals("3 Days Before"))
        {
            c.add(Calendar.DATE,-3);
        }
        else
        {
            c.add(Calendar.DATE,-7);
        }

        returnDate = df.format(c.getTime());
        return returnDate;
    }

    public void displayLoginDetails()
    {
        Subscription detailsToView = new Gson().fromJson(getIntent().getStringExtra("Subscription"),Subscription.class);
        String passwordToView = detailsToView.getPassword();
        String emailToView = detailsToView.getEmail();

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewSubscriptionActivity.this);
        builder.setMessage("Password: " + passwordToView + "\nEmail: " + emailToView);
        builder.setTitle("Your Credentials");

        builder.setNeutralButton("Return",(DialogInterface.OnClickListener) (dialog, which) -> {dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void removeSubscription(String subName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you sure you want to delete this");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("subscriptions")
                            .document(user.getEmail())
                            .collection("subscriptions")
                            .document(subName)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(ViewSubscriptionActivity.this, "Delete Successed", Toast.LENGTH_SHORT).show();

                                    startMainActivity();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xóa thất bại, hiển thị thông báo hoặc thực hiện các hoạt động khác
                                    Toast.makeText(ViewSubscriptionActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void startMainActivity() {
        Intent intent = new Intent(ViewSubscriptionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
