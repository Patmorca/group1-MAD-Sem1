package com.example.subscribe;

import java.util.Date;

public class Subscription {

    public   String subName;
    public   String frequency;
    public Date startDate;
    public String reminder;
    public float cost;
    public   String email;
    public   String password;
    public Subscription (){

    }

    public Subscription(String subName, String frequency, Date startDate, String reminder, float cost, String email, String password)
    {
        this.subName = subName;
        this.frequency = frequency;
        this.startDate = startDate;
        this.reminder = reminder;
        this.cost = cost;
        this.email = email;
        this.password = password;
    }

    public String getSubName() {return subName;}
    public String getFrequency() {return frequency;}
    public Date getStartDate() {return startDate;}
    public String getReminder() {return reminder;}
    public float getCost() {return cost;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
}
