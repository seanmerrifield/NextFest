package com.example.android.nextfest.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Event extends RealmObject{
    //Every event belongs to a venue
    private Venue venue;
    private Location location;

    private String type;
    private String eventName;
    @Required
    private String headliner;

    @PrimaryKey
    private int songkickId;

    private long date;
    private long time;

    public String getType(){return this.type;}
    public void setType(String type){this.type = type;}

    public String getEventName(){return this.eventName;}
    public void setEventName(String eventName){this.eventName = eventName;}

    public String getHeadliner(){return this.headliner;}
    public void setHeadliner(String headliner){this.headliner = headliner;}

    public int getSongkickId(){return this.songkickId;}
    public void setSongkickId(int songkickId){this.songkickId = songkickId;}

    public long getDate(){return this.date;}
    public void setDate(long date){this.date = date;}

    public long getTime(){return this.time;}
    public void setTime(long time){this.time = time;}

    public Venue getVenue(){return this.venue;}
    public void setVenue(Venue venue){this.venue = venue;}

    public Location getLocation(){return this.location;}
    public void setLocation(Location location){this.location = location;}
}
