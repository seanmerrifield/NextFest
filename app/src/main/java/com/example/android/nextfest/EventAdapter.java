package com.example.android.nextfest;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.nextfest.data.Event;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class EventAdapter extends RealmBaseAdapter<Event> implements ListAdapter{

    static final String LOG_TAG = EventAdapter.class.getSimpleName();

    public static class EventViewHolder {
        TextView name;
    }

    public EventAdapter(Context context, RealmResults<Event> realmResults, boolean automaticUpdate){
        super(context, realmResults,automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        EventViewHolder  viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.fragment_festival, parent, false);
            viewHolder = new EventViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.list_item_festival_textview);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (EventViewHolder) convertView.getTag();
        }

        Event event = realmResults.get(position);
        viewHolder.name.setText(event.getHeadliner() + " at " + event.getVenue().getVenueName()+ ", " + event.getVenue().getLocation().getCity() + " on " + Utility.formatDatetoString(event.getDate()));
        return convertView;
    }

    public RealmResults<Event> getRealmResults() {
        return realmResults;
    }
}
