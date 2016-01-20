package com.example.android.nextfest;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FestivalAdapter extends CursorAdapter{
    public FestivalAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor){
        String date = Utility.formatDatetoString(cursor.getLong(FestivalActivityFragment.COL_START_DATE));
        String headliner = cursor.getString(FestivalActivityFragment.COL_HEADLINER);
        String eventName = cursor.getString(FestivalActivityFragment.COL_EVENT_NAME);
        return  date + " - " + headliner + " - " + eventName;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_festival, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView textView = (TextView)view;
        textView.setText(convertCursorRowToUXFormat(cursor));
    }

}
