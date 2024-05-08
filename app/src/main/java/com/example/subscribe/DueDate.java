package com.example.subscribe;

import java.util.Calendar;
import java.util.Date;

public class DueDate {

    public static Calendar dueDateAAS(Subscription subscription)
    {
        Calendar remCalendar = Calendar.getInstance();
        Date startDate = subscription.getStartDate();
        remCalendar.setTime(startDate);

        String remFreq = subscription.getFrequency();
        String nextRem = subscription.getReminder();

        if(remFreq.equals("30 Days"))
        {
            remCalendar.add(Calendar.DATE,30);
        }
        else if(remFreq.equals("60 Days"))
        {
            remCalendar.add(Calendar.DATE,60);
        }
        else if(remFreq.equals("90 Days"))
        {
            remCalendar.add(Calendar.DATE,90);
        }
        else if(remFreq.equals("180 Days"))
        {
            remCalendar.add(Calendar.DATE,180);
        }
        else if (remFreq.equals("1 Month"))
        {
            remCalendar.add(Calendar.MONTH,1);
        }
        else if (remFreq.equals("3 Months"))
        {
            remCalendar.add(Calendar.MONTH,3);
        }
        else if (remFreq.equals("6 Months"))
        {
            remCalendar.add(Calendar.MONTH,6);
        }
        else if (remFreq.equals("12 Months"))
        {
            remCalendar.add(Calendar.MONTH,12);
        }

        if(nextRem.equals("In 15 Seconds"))
        {
            Calendar remTestCalendar = Calendar.getInstance();
            remTestCalendar.add(Calendar.SECOND,15);
            return remTestCalendar;
        }
        else if(nextRem.equals("The Day Before"))
        {
            remCalendar.add(Calendar.DATE,-1);
        }
        else if(nextRem.equals("3 Days Before"))
        {
            remCalendar.add(Calendar.DATE,-3);
        }
        else
        {
            remCalendar.add(Calendar.DATE,-7);
        }

        return remCalendar;

    }

}