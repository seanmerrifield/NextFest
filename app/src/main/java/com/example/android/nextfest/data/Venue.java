package com.example.android.nextfest.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Venue extends RealmObject {

    //Every venue belongs to a location
    private Location location;

    @PrimaryKey
    private String venueName;
    private double latitude;
    private double longitude;

    public String getVenueName(){ return this.venueName;}
    public void setVenueName(String venueName){this.venueName = venueName;}

    public double getLatitude(){return this.latitude;}
    public void setLatitude(double latitude){ this.latitude = latitude;}

    public double getLongitude(){return this.longitude;}
    public void setLongitude(double  longitude){ this.longitude = longitude;}

    public Location getLocation(){return this.location;}
    public void setLocation(Location location){this.location = location;}



}
