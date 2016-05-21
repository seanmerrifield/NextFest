package com.example.android.nextfest.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Artist extends RealmObject {

    //Artist has many tracks

    @PrimaryKey
    private String artistName;

    private String spotifyId;
    private String imageUrl;


    public String getArtistName(){return this.artistName;}
    public String getSpotifyId(){return this.spotifyId;}
    public String getImageUrl(){return this.imageUrl;}


    public void setArtistName(String artist){this.artistName = artist;}
    public void setSpotifyId(String id){this.spotifyId = id;}
    public void setImageUrl(String url){this.imageUrl = url;}

}
