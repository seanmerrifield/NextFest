package com.example.android.nextfest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


    public FestivalActivityFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);

        //Retrieve event data
        Realm realm = Realm.getDefaultInstance();
        //RealmResults<Location> locationResult = realm.where(Location.class).equalTo("locationSetting", "31366").findAll();
        //RealmResults<Venue> venueResult = realm.where(Venue.class).equalTo("location.locationSetting", "31366").findAll();
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
        FetchFestivalTask festivalTask = new FetchFestivalTask(getActivity());
        festivalTask.execute("31366");
    }

    @Override
    public void onStart(){
        super.onStart();
        updateFestivals();

    }


/*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        String sortOrder = FestivalContract.EventEntry.COLUMN_DATE + " ASC";

        return new CursorLoader(getActivity(),
                FestivalContract.EventEntry.CONTENT_URI,
                EVENT_COLUMNS,
                null,
                null,
                sortOrder
                );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mFestivalAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mFestivalAdapter.swapCursor(null);
    }
*/

}
