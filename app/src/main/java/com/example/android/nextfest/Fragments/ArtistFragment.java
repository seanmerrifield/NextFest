package com.example.android.nextfest.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.nextfest.Activities.EventDetailActivity;
import com.example.android.nextfest.Adapters.ArtistAdapter;
import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Artist;
import com.example.android.nextfest.data.Event;

import io.realm.RealmResults;

public class ArtistFragment extends Fragment {

    private ArtistAdapter mArtistAdapter;
    private final String LOG_TAG = ArtistFragment.class.getSimpleName();

    public ArtistFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);

        Event event = ((EventDetailActivity) getActivity()).getEvent();

        RealmResults<Artist> artists = event.getArtists().where().findAll();
        mArtistAdapter = new ArtistAdapter(getActivity(), artists, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);

        return rootView;

    }

}
