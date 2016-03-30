package com.example.android.nextfest.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.nextfest.Activities.EventDetailActivity;
import com.example.android.nextfest.Activities.LogInActivity;
import com.example.android.nextfest.AsyncTasks.FetchPlaylistTask;
import com.example.android.nextfest.MyApplication;
import com.example.android.nextfest.R;
import com.example.android.nextfest.SpotifyService;
import com.example.android.nextfest.data.Artist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class PlaylistFragment extends Fragment {

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

                //Try initalizing Spotify Player. If initialization fails send to LogIn Activity
                try {
                    spotifyService.initializePlayer();
                    spotifyService.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");

                } catch(RuntimeException e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title);

                    builder.setPositiveButton(R.string.action_log_in, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getActivity(), LogInActivity.class));
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
        return rootView;
    }

    private void obtainPlaylist(String artistName){
        Realm realm = Realm.getDefaultInstance();

        if (artistName == null || artistName.isEmpty()){
            throw new NullPointerException("Artist name is empty");
        }
        else{
            Artist artist = realm.where(Artist.class).equalTo("artistName", artistName).findFirst();

            Log.v(LOG_TAG, "obtainPlaylist: Artist ID is " + artist.getSpotifyId());
            FetchPlaylistTask playlistTask = new FetchPlaylistTask(getActivity());
            playlistTask.execute(artist.getSpotifyId(), artist.getArtistName());
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        //Get artist name from Event Detail Activity and obtain artist playlist
        String artistName = ((EventDetailActivity) getActivity()).getArtist();
        Log.v(LOG_TAG, "onStart: Artist Name Obtained From Event Detail Activity is " + artistName);
        try {
            obtainPlaylist(artistName);
        }
        catch(NullPointerException e){
            Log.e(LOG_TAG, "Artist Name is empty. Cannot obtain playlist.", e);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SpotifyService spotifyService = ((MyApplication) this.getActivity().getApplication()).getSpotifyService();
        spotifyService.destroyPlayer();
    }
}
