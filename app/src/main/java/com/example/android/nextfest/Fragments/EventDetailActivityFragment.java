package com.example.android.nextfest.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Event;

import io.realm.Realm;

public class EventDetailActivityFragment extends Fragment {

    public EventDetailActivityFragment(){
        setHasOptionsMenu(true);
    }
    private final String LOG_TAG = EventDetailActivityFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        int eventId = intent.getIntExtra("event_id", 0);
        if (eventId != 0 ) {
            Realm realm = Realm.getDefaultInstance();
            Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            ((TextView) rootView.findViewById(R.id.event_detail_headliner_text)).setText(event.getHeadliner());
            ((TextView) rootView.findViewById(R.id.event_detail_venue_text)).setText(event.getVenue().getVenueName());
            ((TextView) rootView.findViewById(R.id.event_detail_date_text)).setText(
                                                                            event.getVenue().getLocation().getCity() + ", " +
                                                                            event.getVenue().getLocation().getCountry());
        }
        return rootView;

    }


}
