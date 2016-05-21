package com.example.android.nextfest.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.nextfest.R;
import com.example.android.nextfest.data.Artist;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ArtistAdapter extends RealmBaseAdapter<Artist> implements ListAdapter {
    static final String LOG_TAG = TrackAdapter.class.getSimpleName();

    public static class ArtistViewHolder{
        TextView artist;
    }

    public ArtistAdapter(Context context, RealmResults<Artist> artists, boolean automaticUpdate){
        super(context, artists, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistViewHolder  viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
            viewHolder = new ArtistViewHolder();
            viewHolder.artist = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ArtistViewHolder) convertView.getTag();
        }

        Artist artist = realmResults.get(position);
        viewHolder.artist.setText(artist.getArtistName());

        //viewHolder.name.setText(event.getHeadliner() + " at " + event.getVenue().getVenueName()+ ", " + event.getVenue().getLocation().getCity() + " on " + Utility.formatDatetoString(event.getDate()));
        return convertView;
    }
}
