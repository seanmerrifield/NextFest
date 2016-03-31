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
import android.widget.ListView;

import com.example.android.nextfest.Activities.EventDetailActivity;
import com.example.android.nextfest.Activities.LogInActivity;
import com.example.android.nextfest.Adapters.TrackAdapter;
import com.example.android.nextfest.AsyncTasks.FetchPlaylistTask;
import com.example.android.nextfest.MyApplication;
import com.example.android.nextfest.R;
import com.example.android.nextfest.SpotifyService;
import com.example.android.nextfest.data.Artist;
import com.example.android.nextfest.data.Track;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlaylistFragment extends Fragment {

    private TrackAdapter mTrackAdapter;
    private final String LOG_TAG = PlaylistFragment.class.getSimpleName();


    public PlaylistFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

        //Get artist name from Event Detail Activity and obtain artist playlist
        String artistName = ((EventDetailActivity) getActivity()).getArtist();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Track> trackResult = realm.where(Track.class).equalTo("artistName", artistName).findAll();


        mTrackAdapter = new TrackAdapter(getActivity(), trackResult, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_playlist);
        listView.setAdapter(mTrackAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventDetailActivity eventDetailActivity = ((EventDetailActivity) getActivity());
                Track track = mTrackAdapter.getItem(position);

                SpotifyService spotifyService = ((MyApplication) eventDetailActivity.getApplication()).getSpotifyService();

                //Try initalizing Spotify Player. If initialization fails send to LogIn Activity
                try {
                    spotifyService.initializePlayer();
                    Log.v(LOG_TAG, "Spotify Play Request: " + track.getSpotifyId());
                    spotifyService.play("spotify:track:" + track.getSpotifyId());
                    //spotifyService.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");

                } catch (RuntimeException e) {
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

        realm.close();
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
