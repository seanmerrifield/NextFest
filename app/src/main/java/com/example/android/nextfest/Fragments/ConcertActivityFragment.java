package com.example.android.nextfest.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.nextfest.EventAdapter;
import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Event;

import io.realm.Realm;
import io.realm.RealmResults;

public class ConcertActivityFragment extends Fragment {
    private EventAdapter mConcertAdapter;

    public ConcertActivityFragment(){
        //Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_venues, container, false);

        //Query database
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> concertResult = realm.where(Event.class).equalTo("type", "Concert").findAll();
        //concertResult.sort("venueName", Sort.ASCENDING);

        //Attach query results to adapter
        mConcertAdapter = new EventAdapter(getActivity(), concertResult, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_venue);
        listView.setAdapter(mConcertAdapter);


        return rootView;



    }
}
