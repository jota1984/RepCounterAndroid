package com.monsalvetoledo.repcounter;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Workout {
    private  int mPushups;
    private  int mSquats;
    private  String mMonth;
    private  int mDay;

    public Workout(String month, int day, int pushups, int squats){
        mMonth = month;
        mDay = day;
        mPushups = pushups;
        mSquats = squats;
    }

    public Workout(String dateStr, int pushups, int squats){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", " Dec"};
        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr);
            mMonth = months[date.getMonth()];
            mDay = date.getDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mPushups = pushups;
        mSquats = squats;
    }

    public String getMonth(){
        return mMonth;
    }

    public int getDay(){
        return mDay;
    }

    public int getPushups(){
        return mPushups;
    }

    public int getSquats(){
        return mSquats;
    }
}
