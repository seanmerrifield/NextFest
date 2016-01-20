package com.example.android.nextfest.data;


import android.content.ContentResolver;
import android.content.ContentUris;
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
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_HEADLINER = "headliner";


        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;


        // builds '/event' path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();


        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // builds '/event/venue_name' path
        public static Uri buildEventVenue(String venueSetting) {
            return CONTENT_URI.buildUpon().appendPath(venueSetting).build();
        }


        public static String getVenueFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }



    }

    public static final class VenueEntry implements BaseColumns {

        public static final String TABLE_NAME = "venue";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_LOCATION_KEY = "location_id";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENUE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENUE;

        //builds '/venue/'
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VENUE).build();

        //builds '/venue/id'
        public static Uri buildVenueUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //builds '/venue/city_name/'
        public static Uri buildVenueLocation(String cityname) {
            return CONTENT_URI.buildUpon().appendPath(cityname).build();
        }


        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        //builds '/location'
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        //builds '/location/id'
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }



}
