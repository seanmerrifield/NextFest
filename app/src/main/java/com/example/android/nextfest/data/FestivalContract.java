package com.example.android.nextfest.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class FestivalContract {


    public static final String CONTENT_AUTHORITY = "com.example.android.nextfest";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EVENT = "event";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_VENUE = "venue";

    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "event";

        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_VENUE_KEY = "venue_id";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
    }

    public static final class VenueEntry implements BaseColumns {

        public static final String TABLE_NAME = "venue";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_LOCATION_KEY = "location_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VENUE).build();
    }

    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
    }



}
