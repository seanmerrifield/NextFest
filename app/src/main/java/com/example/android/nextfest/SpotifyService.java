package com.example.android.nextfest;

import com.spotify.sdk.android.player.Player;

public class SpotifyService{
        private static final String CLIENT_ID = "c520385f35d743a9a0f310c82c581736";
        private static final String REDIRECT_URI = "com.example.android.nextfest://callback";
        private static final int REQUEST_CODE = 1337;
        String USER_TOKEN;
        Player mPlayer;

        public SpotifyService(){

        }

        public String getUserToken(){
            return USER_TOKEN;
        }

        public Player getPlayer(){
            return mPlayer;
        }

        public void setUserToken(String userToken){
            USER_TOKEN = userToken;
        }

        public void setPlayer(Player player){
            mPlayer = player;
        }
}

