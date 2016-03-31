package com.example.android.nextfest.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Track;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class TrackAdapter extends RealmBaseAdapter<Track> implements ListAdapter {


    static final String LOG_TAG = TrackAdapter.class.getSimpleName();

    public static class TrackViewHolder{
        TextView track;
    }

    public TrackAdapter(Context context, RealmResults<Track> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackViewHolder  viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.fragment_playlist, parent, false);
            viewHolder = new TrackViewHolder();
            viewHolder.track = (TextView) convertView.findViewById(R.id.list_item_playlist_textview);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (TrackViewHolder) convertView.getTag();
        }

        Track track = realmResults.get(position);
        viewHolder.track.setText(track.getTrackName());

        //viewHolder.name.setText(event.getHeadliner() + " at " + event.getVenue().getVenueName()+ ", " + event.getVenue().getLocation().getCity() + " on " + Utility.formatDatetoString(event.getDate()));
        return convertView;
    }
}
