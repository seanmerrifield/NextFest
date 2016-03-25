package com.example.android.nextfest;

import android.app.Application;

public class MyApplication extends Application {
    private String USER_TOKEN = null;
    private SpotifyService mSpotifyService;

    @Override
    public void onCreate(){
        super.onCreate();
        getSpotifyService();
    }

    public String getUserToken(){
        return USER_TOKEN;
    }

    public SpotifyService getSpotifyService(){
        if (mSpotifyService == null){
            mSpotifyService = new SpotifyService();
        }

        return mSpotifyService;
    }


    public void setUserToken(String userToken){
        this.USER_TOKEN = userToken;
    }
}
