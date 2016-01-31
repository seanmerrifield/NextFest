package com.example.android.nextfest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.nextfest.data.Venue;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class VenueActivityFragment extends Fragment {
    private VenueAdapter mVenueAdapter;

    public VenueActivityFragment(){
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
        RealmResults<Venue> venueResult = realm.where(Venue.class).findAll();
        venueResult.sort("venueName", Sort.ASCENDING);

        //Attach query results to adapter
        mVenueAdapter = new VenueAdapter(getActivity(), venueResult, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_venue);
        listView.setAdapter(mVenueAdapter);


        return rootView;



    }
}
