package com.example.android.nextfest.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Artist extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String artistName;

    @Required
    private String spotifyId;

    private String imageUrl;

    public int getId(){return id;}
    public String getArtistName(){return this.artistName;}
    public String getSpotifyId(){return this.spotifyId;}
    public String getImageUrl(){return this.imageUrl;}

    public void setId(int artistId){this.id = artistId;}
    public void setArtistName(String artist){this.artistName = artist;}
    public void setSpotifyId(String id){this.spotifyId = id;}
    public void setImageUrl(String url){this.imageUrl = url;}

}
