package com.example.android.nextfest;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LogInActivity extends AppCompatActivity {
    private final String LOG_TAG = LogInActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Log in user with Spotify
        SpotifyService spotifyService = ((MyApplication) this.getApplication()).getSpotifyService();
        spotifyService.openLogIn(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d(LOG_TAG, "Spotify Login returned with with resultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, intent);
        SpotifyService spotifyService = ((MyApplication) this.getApplication()).getSpotifyService();

        try {
            spotifyService.verifyLogIn(requestCode, resultCode, intent);
            startActivity(new Intent(this, FestivalActivity.class));

        } catch (RuntimeException e) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(e.getMessage())
                    .setTitle(R.string.error_title);

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

}
