package com.example.android.nextfest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class LogInActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    private static final String CLIENT_ID = "c520385f35d743a9a0f310c82c581736";
    private static final String REDIRECT_URI = "com.example.android.nextfest://callback";
    private static final int REQUEST_CODE = 1337;

    private static String USER_TOKEN;
    private Player mPlayer;
    private final String LOG_TAG = LogInActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Spotify User Authentication builder
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(  CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private","streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d(LOG_TAG, "Starting onActivityResult with resultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if(response.getType() == AuthenticationResponse.Type.TOKEN) {
                //Retrieve user token and set to global variable
                USER_TOKEN = response.getAccessToken();

                SpotifyService spotifyService = ((MyApplication) this.getApplication()).getSpotifyService();

                Log.v(LOG_TAG, "Authentication Successful");


                Config playerConfig = new Config(this, USER_TOKEN, CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(LogInActivity.this);
                        mPlayer.addPlayerNotificationCallback(LogInActivity.this);
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(LOG_TAG, "Could not initialize player: " + throwable.getMessage());
                    }
                });

                spotifyService.setPlayer(mPlayer);
                spotifyService.setUserToken(USER_TOKEN);
                startActivity(new Intent(this, FestivalActivity.class));
            }
            else if(response.getType() == AuthenticationResponse.Type.ERROR){
                Log.v(LOG_TAG, "Authentication Error");
            }
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
        Log.d(LOG_TAG, "Received connection message: "+ message);
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
