package com.example.subscribe;

import java.util.Date;

public class Subscription {

    //Data members
    private   String subName;
    private   String frequency;
    private Date startDate;
    private String reminder;
    private float cost;
    private  String email;
    private   String password;
    public Subscription (){

    } //Default constructor

    public Subscription(String subName, String frequency, Date startDate, String reminder, float cost, String email, String password) //Object constructor
    {
        this.subName = subName;
        this.frequency = frequency;
        this.startDate = startDate;
        this.reminder = reminder;
        this.cost = cost;
        this.email = email;
        this.password = password;
    }

    //Getters
    public String getSubName() {return subName;}
    public String getFrequency() {return frequency;}
    public Date getStartDate() {return startDate;}
    public String getReminder() {return reminder;}
    public float getCost() {return cost;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
}
