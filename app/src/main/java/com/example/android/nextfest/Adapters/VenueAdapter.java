package com.example.android.nextfest.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Venue;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class VenueAdapter extends RealmBaseAdapter<Venue> implements ListAdapter {

    static final String LOG_TAG = VenueAdapter.class.getSimpleName();

    public static class VenueViewHolder{
        TextView venue;
    }

    public VenueAdapter(Context context, RealmResults<Venue> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VenueViewHolder  viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.fragment_venues, parent, false);
            viewHolder = new VenueViewHolder();
            viewHolder.venue = (TextView) convertView.findViewById(R.id.venue_item_textview);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (VenueViewHolder) convertView.getTag();
        }

        Venue venue = realmResults.get(position);
        viewHolder.venue.setText(venue.getVenueName());

        //viewHolder.name.setText(event.getHeadliner() + " at " + event.getVenue().getVenueName()+ ", " + event.getVenue().getLocation().getCity() + " on " + Utility.formatDatetoString(event.getDate()));
        return convertView;
    }

}
