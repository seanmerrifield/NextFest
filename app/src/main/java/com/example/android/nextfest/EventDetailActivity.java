package com.example.android.nextfest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class EventDetailActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    private static final String CLIENT_ID = "c520385f35d743a9a0f310c82c581736v";
    private static final String REDIRECT_URI = "com.example.android.nextfest://callback";
    private static final int REQUEST_CODE = 1234;
    private final String LOG_TAG = EventDetailActivity.class.getSimpleName();
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Spotify User Authentication builder
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(  CLIENT_ID,
                                                                                    AuthenticationResponse.Type.TOKEN,
                                                                                    REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if(response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver(){
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(EventDetailActivity.this);
                        mPlayer.addPlayerNotificationCallback(EventDetailActivity.this);
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(LOG_TAG, "Could not initialize player: " + throwable.getMessage());
                    }
                });
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
        Log.d("MainActivity", "Login failed");
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
