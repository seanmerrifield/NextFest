package com.example.android.nextfest;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.nextfest.data.Event;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class FestivalActivityFragment extends Fragment{

    private EventAdapter mEventAdapter;
    private final String LOG_TAG = FestivalActivity.class.getSimpleName();

    public FestivalActivityFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);

        //Retrieve event data
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventResult = realm.where(Event.class).greaterThanOrEqualTo("date",System.currentTimeMillis()).findAll();
        eventResult.sort("date", Sort.ASCENDING);

        //Attach data to list view
        mEventAdapter = new EventAdapter(getActivity(), eventResult, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_festival);
        listView.setAdapter(mEventAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Event event = mEventAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), EventDetailActivity.class)
                        .putExtra("event_id", event.getId());
                startActivity(intent);
            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //getLoaderManager().initLoader(FESTIVAL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private void updateFestivals(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationSetting = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        Log.v(LOG_TAG, "Updating Festival Data with location setting " + locationSetting);
        FetchFestivalTask festivalTask = new FetchFestivalTask(getActivity());
        festivalTask.execute(locationSetting);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateFestivals();

    }


}
