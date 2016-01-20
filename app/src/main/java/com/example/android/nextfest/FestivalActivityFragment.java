package com.example.android.nextfest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.nextfest.data.FestivalContract;


public class FestivalActivityFragment extends Fragment{

    private static final int FESTIVAL_LOADER = 0;
    private FestivalAdapter mFestivalAdapter;

    private static final String[] EVENT_COLUMNS = {
            FestivalContract.EventEntry.TABLE_NAME + "." + FestivalContract.EventEntry._ID,
            FestivalContract.EventEntry.COLUMN_VENUE_KEY,
            FestivalContract.EventEntry.COLUMN_EVENT_NAME,
            FestivalContract.EventEntry.COLUMN_HEADLINER,
            FestivalContract.EventEntry.COLUMN_DATE,
            FestivalContract.EventEntry.COLUMN_TIME
    };

    static final int COL_EVENT_ID = 0;
    static final int COL_VENUE_ID = 1;
    static final int COL_EVENT_NAME = 2;
    static final int COL_HEADLINER = 3;
    static final int COL_START_DATE = 4;
    static final int COL_END_DATE = 5;


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
            updateFestivals();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);


        mFestivalAdapter = new FestivalAdapter(getActivity(), null, 0);

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
        //updateFestivals();

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
