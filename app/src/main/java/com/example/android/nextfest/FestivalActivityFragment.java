package com.example.android.nextfest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class FestivalActivityFragment extends Fragment {

    private ArrayAdapter<String> mFestivalAdapter;

    public FestivalActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.festivalfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            FetchFestivalTask festivalTask = new FetchFestivalTask(getActivity(),mFestivalAdapter);
            festivalTask.execute("31366");
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);

        ArrayList<String> test_festivals = new ArrayList<String>();
        test_festivals.add("EDC - Las Vegas - Jun 2015");
        test_festivals.add("Coachella - California - Apr 2015");
        test_festivals.add("Tomorrowland - Belgium - Jul 2015");


       mFestivalAdapter = new ArrayAdapter<String>(
                //sets context
                getActivity(),
                R.layout.list_item_festival,
                R.id.list_item_festival_textview,
                test_festivals
        );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_festival);

        listView.setAdapter(mFestivalAdapter);

        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              String festival = mFestivalAdapter.getItem(position);
               Intent intent = new Intent(getActivity(), FestivalDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, festival);
                startActivity(intent);
            }
        });
           */
        return rootView;
    }

    private void updateFestivals(){
        FetchFestivalTask festivalTask = new FetchFestivalTask(getActivity(),mFestivalAdapter);
        festivalTask.execute("31366");
    }

    @Override
    public void onStart(){
        super.onStart();
        updateFestivals();

    }


}
