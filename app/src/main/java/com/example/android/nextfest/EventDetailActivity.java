package com.example.android.nextfest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.nextfest.data.Event;

import io.realm.Realm;

public class EventDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = EventDetailActivity.class.getSimpleName();
    private String mArtist;

    public String getArtist(){
        return mArtist;
    }

    private void obtainArtist(String artistName) {
        //Retrieve artist data
        FetchArtistTask artistTask = new FetchArtistTask(this);
        artistTask.execute(artistName);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve artist data from event name
        int event_id = getIntent().getIntExtra("event_id", 0);
        Realm realm = Realm.getDefaultInstance();
        Event event = realm.where(Event.class).equalTo("id", event_id).findFirst();
        mArtist = event.getHeadliner();
        //Obtain Artsit data from Spotify and add to artist model
        obtainArtist(mArtist);

        setContentView(R.layout.activity_event_detail);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();

    }



}
