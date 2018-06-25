package com.monsalvetoledo.repcounter;

public class Workout {
    public int pushups;
    public int squats;
    public String month;
    public int day;

    public Workout(String month, int day, int pushups, int squats){
        this.month = month;
        this.day = day;
        this.pushups = pushups;
        this.squats = squats;
    }

    public String getMonth(){
        return month;
    }

    public int getDay(){
        return day;
    }

    public int getPushups(){
        return pushups;
    }

    public int getSquats(){
        return squats;
    }
}
