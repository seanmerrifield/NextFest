package com.example.android.nextfest.data;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Venue extends RealmObject {

    private RealmList<Event> events;


    @PrimaryKey
    private String venueName;
    private Location location;

    public RealmList<Event> getEvents(){return this.events;}
    public void setEvents(RealmList<Event> events){this.events = events;}

    public String getVenueName(){ return this.venueName;}
    public void setVenueName(String venueName){this.venueName = venueName;}


    public Location getLocation(){return this.location;}
    public void setLocation(Location location){this.location = location;}



}
