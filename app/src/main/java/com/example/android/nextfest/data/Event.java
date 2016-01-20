package com.example.android.nextfest.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject{


    @PrimaryKey
    private String eventName;
    private String headliner;
    private long date;
    private long time;

    public String getEventName(){return this.eventName;}
    public void setEventName(String eventName){this.eventName = eventName;}

    public String getHeadliner(){return this.headliner;}
    public void setHeadliner(String headliner){this.headliner = headliner;}

    public long getDate(){return this.date;}
    public void setDate(long date){this.date = date;}

    public long getTime(){return this.time;}
    public void setTime(long time){this.time = time;}

}
