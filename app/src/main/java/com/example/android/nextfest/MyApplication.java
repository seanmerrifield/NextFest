package com.example.android.nextfest;

import android.app.Application;

public class MyApplication extends Application {
    private SpotifyService mSpotifyService;

    @Override
    public void onCreate(){
        super.onCreate();
        getSpotifyService();
    }


    public SpotifyService getSpotifyService(){
        if (mSpotifyService == null){
            mSpotifyService = new SpotifyService(getApplicationContext());
        }

        return mSpotifyService;
    }

}
