package com.example.android.nextfest.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Track extends RealmObject{

    @PrimaryKey
    private int id;
    @Required
    private String trackName;
    @Required
    private String artistName;
    @Required
    private String spotifyId;


    //Get Methods
    public int getId(){return id;}
    public String getTrackName(){return this.trackName;}
    public String getSpotifyId(){return this.spotifyId;}
    public String getArtistName(){return this.artistName;}

    //Setter Methods
    public void setId(int trackId){this.id = trackId;}
    public void setTrackName(String trackName){this.trackName = trackName;}
    public void setArtistName(String artistName){this.artistName = artistName;}
    public void setSpotifyId(String id){this.spotifyId = id;}


}
