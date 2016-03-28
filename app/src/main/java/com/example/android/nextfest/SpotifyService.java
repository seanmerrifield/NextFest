package com.example.android.nextfest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class SpotifyService implements
        PlayerNotificationCallback, ConnectionStateCallback {
        private static final String CLIENT_ID = "c520385f35d743a9a0f310c82c581736";
        private static final String REDIRECT_URI = "com.example.android.nextfest://callback";
        private static final int REQUEST_CODE = 1337;
        private final String LOG_TAG = FestivalActivity.class.getSimpleName();
        private String USER_TOKEN;
        private Player mPlayer;
        private Context context;

        public SpotifyService(Context context){
            this.context = context;
        }

        public String getUserToken(){
            return USER_TOKEN;
        }


        public void openLogIn(Activity activity){
            //Spotify User Authentication builder
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(  CLIENT_ID,
                                                                                        AuthenticationResponse.Type.TOKEN,
                                                                                        REDIRECT_URI);

            builder.setScopes(new String[]{"user-read-private","streaming"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);

        }

        public void verifyLogIn(int requestCode, int resultCode, Intent intent){

            if (requestCode == REQUEST_CODE){
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

                if(response.getType() == AuthenticationResponse.Type.TOKEN) {
                    setUserToken(response.getAccessToken());
                }
                else if(response.getType() == AuthenticationResponse.Type.ERROR){
                    Log.v(LOG_TAG, "Authentication Error");
                    throw new RuntimeException(response.getError());
                }
            }
        }

        public void initializePlayer(){

            //Return if player is initialized already
            if (mPlayer instanceof Player) {
                return;
            }

            //User Token needs to exist to initialize player
            if (USER_TOKEN != null && !USER_TOKEN.isEmpty()) {
                Config playerConfig = new Config(context, USER_TOKEN, CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {

                        player.addConnectionStateCallback(SpotifyService.this);
                        player.addPlayerNotificationCallback(SpotifyService.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(LOG_TAG, "Could not initialize player: " + throwable.getMessage());
                        throw new RuntimeException("Could not initialize player: " + throwable.getMessage(), throwable);
                    }
                });
            }
            else{
                Log.e(LOG_TAG, "User Not Logged In");
                throw new NullPointerException("You are not logged into Spotify");
            }
        }

        public void setUserToken(String userToken){
            USER_TOKEN = userToken;
        }


    public void play(String song)
    {
        mPlayer.play(song);
    }

    public void destroyPlayer(){
        //If player exists then destroy player
        if(mPlayer instanceof Player){
            Spotify.destroyPlayer(mPlayer);
        }
        else{
            Log.v(LOG_TAG, "Spotify Player is not initalized. Nothing to destroy");
        }
    }




    @Override
    public void onLoggedIn(){
        Log.d(LOG_TAG, "User logged in");
    }

    @Override
    public void onLoggedOut(){
        Log.d(LOG_TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(LOG_TAG, "Login failed");
    }


    @Override
    public void onTemporaryError(){
        Log.d(LOG_TAG, "Temporary error occured");
    }

    @Override
    public void onConnectionMessage(String message){
        Log.d(LOG_TAG, "Received connection message: " + message);
    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState){
        Log.d(LOG_TAG, "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails){
        Log.d(LOG_TAG, "Playback error received: " + errorType.name());
    }
}

