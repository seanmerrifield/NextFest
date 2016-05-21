package com.example.android.nextfest.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.nextfest.Activities.EventDetailActivity;
import com.example.android.nextfest.EventAdapter;
import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Event;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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

        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);

        //Query database
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> concertResult = realm.where(Event.class).equalTo("type", "Concert").greaterThanOrEqualTo("date", System.currentTimeMillis()).findAll();
        concertResult.sort("date", Sort.ASCENDING);

        //Attach query results to adapter
        mConcertAdapter = new EventAdapter(getActivity(), concertResult, true);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_festival);
        listView.setAdapter(mConcertAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event event = mConcertAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), EventDetailActivity.class)
                        .putExtra("event_id", event.getSongkickId());
                startActivity(intent);
            }
        });

        return rootView;



    }
}
