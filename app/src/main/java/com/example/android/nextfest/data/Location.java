package com.example.android.nextfest.data;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Location extends RealmObject {

    private RealmList<Venue> venues;

    @PrimaryKey
    private long locationSetting;

    @Required
    private String city;
    @Required
    private String country;

    private double latitude;
    private double longitude;

    public RealmList<Venue> getVenues(){return this.venues;}
    public void setVenues(RealmList<Venue> venues){this.venues = venues;}

    public long getLocationSetting(){return this.locationSetting;}
    public void setLocationSetting(long locationSetting) {this.locationSetting = locationSetting;}

    public String getCity(){return this.city;}
    public void setCity(String city) {this.city = city;}

    public String getCountry(){return this.country;}
    public void setCountry(String country) {this.country = country;}

    public double getLatitude(){return this.latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude(){return this.longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}
}


