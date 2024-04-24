package com.example.subscribe;

import java.util.Date;

public class Subscription {

    private String name;
    private String frequency;
    private Date startDate;
    private String reminder;
    private float cost;
    private String email;
    private String password;

    public Subscription(String name, String frequency, Date startDate, String reminder, float cost, String email, String password)
    {
        this.name = name;
        this.frequency = frequency;
        this.startDate = startDate;
        this.reminder = reminder;
        this.cost = cost;
        this.email = email;
        this.password = password;
    }

    public String getSubName() {return name;}
    public String getFrequency() {return frequency;}
    public Date getStartDate() {return startDate;}
    public String getReminder() {return reminder;}
    public float getCost() {return cost;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
}
