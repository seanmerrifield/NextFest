package com.example.android.nextfest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = EventDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_detail);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Retrieve logged in user token for spotify player
        SpotifyService spotifyService = ((MyApplication) this.getApplication()).getSpotifyService();
        Log.v(LOG_TAG, "User Token Is: " + spotifyService.getUserToken());

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



    public static class PlaylistFragment extends Fragment {

        public PlaylistFragment() {
            setHasOptionsMenu(true);
        }

        private final String LOG_TAG = PlaylistFragment.class.getSimpleName();


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);



            String[] playlistArray = {
                    "Above & Beyond - Good For Me"
            };

            List<String> playlist = new ArrayList<String>(
                    Arrays.asList(playlistArray)
            );

            //Attach data to list view
            final ArrayAdapter playlistAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.fragment_playlist,
                    R.id.list_item_playlist_textview,
                    playlist);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_playlist);
            listView.setAdapter(playlistAdapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EventDetailActivity eventDetailActivity = ((EventDetailActivity) getActivity());

                    SpotifyService spotifyService = ((MyApplication) eventDetailActivity.getApplication()).getSpotifyService();
                    Log.v(LOG_TAG, "User Token Is: " + spotifyService.getUserToken());
                    Player player = spotifyService.getPlayer();
                    player.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                }
            });
            return rootView;
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }
}
