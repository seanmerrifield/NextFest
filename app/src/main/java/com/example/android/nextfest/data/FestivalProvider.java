package com.example.android.nextfest.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class FestivalProvider extends ContentProvider{

    public static final String LOG_TAG = FestivalProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FestivalDbHelper mOpenHelper;

    static final int EVENTS = 100;
    static final int EVENT = 101;
    static final int EVENT_WITH_VENUE = 102;
    static final int EVENT_WITH_VENUE_AND_DATE = 103;
    static final int EVENT_WITH_VENUE_AND_START_DATE = 104;

    static final int VENUES = 200;
    static final int VENUE = 201;
    static final int VENUE_WITH_LOCATION = 202;

    static final int LOCATIONS = 300;
    static final int LOCATION = 301;

    private static final SQLiteQueryBuilder sEventByVenueSettingQueryBuilder;
    private static final SQLiteQueryBuilder sVenueByLocationSettingQueryBuilder;
    //Creates JOIN between Event and Venue Tables
    static {
        sEventByVenueSettingQueryBuilder = new SQLiteQueryBuilder();
        sEventByVenueSettingQueryBuilder.setTables(
                FestivalContract.EventEntry.TABLE_NAME + " INNER JOIN " +
                        FestivalContract.VenueEntry.TABLE_NAME +
                        " ON " + FestivalContract.EventEntry.TABLE_NAME +
                        "." + FestivalContract.EventEntry.COLUMN_VENUE_KEY +
                        " = " + FestivalContract.VenueEntry.TABLE_NAME +
                        "." + FestivalContract.VenueEntry._ID);

    }

    static {
        sVenueByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sEventByVenueSettingQueryBuilder.setTables(
                FestivalContract.VenueEntry.TABLE_NAME + " INNER JOIN " +
                        FestivalContract.LocationEntry.TABLE_NAME +
                        " ON " + FestivalContract.VenueEntry.TABLE_NAME +
                        "." + FestivalContract.VenueEntry.COLUMN_LOCATION_KEY +
                        " = " + FestivalContract.LocationEntry.TABLE_NAME +
                        "." + FestivalContract.LocationEntry._ID);
    }

    private static final String sVenueSettingSelection =
            FestivalContract.VenueEntry.TABLE_NAME + "." + FestivalContract.VenueEntry.COLUMN_VENUE_NAME + " = ? ";

    //location.location_setting = ?
    private static final String sLocationSettingSelection =
            FestivalContract.LocationEntry.TABLE_NAME + "." + FestivalContract.LocationEntry.COLUMN_CITY + " = ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingWithDate =
            FestivalContract.LocationEntry.TABLE_NAME + "." + FestivalContract.LocationEntry.COLUMN_CITY + " = ?" +
                    " AND " + FestivalContract.EventEntry.COLUMN_START_DATE + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            FestivalContract.LocationEntry.TABLE_NAME + "." + FestivalContract.LocationEntry.COLUMN_CITY + " = ?" +
                    " AND " + FestivalContract.EventEntry.COLUMN_START_DATE + " >= ? ";

    @Override
    public boolean onCreate(){
        mOpenHelper = new FestivalDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FestivalContract.CONTENT_AUTHORITY;

        //matches '/event/'
        matcher.addURI(authority, FestivalContract.PATH_EVENT, EVENTS);

        //matches '/event/id'
        //matcher.addURI(authority, FestivalContract.PATH_EVENT + "/#", EVENT);

        //matches '/event/venue_name'
        matcher.addURI(authority, FestivalContract.PATH_EVENT + "/*", EVENT_WITH_VENUE);

        //matches '/event/id/venue/id/date'
        //matcher.addURI(authority, FestivalContract.PATH_EVENT+ "/#/" + FestivalContract.PATH_VENUE + "/*/#", EVENT_WITH_VENUE_AND_DATE);

        //matches '/venue'
        matcher.addURI(authority, FestivalContract.PATH_VENUE, VENUES);

        //matches '/venue/id'
      //  matcher.addURI(authority, FestivalContract.PATH_VENUE
       //         + "/#", VENUE);

        //matches '/venue/location_name'
        matcher.addURI(authority, FestivalContract.PATH_VENUE + "/*", VENUE_WITH_LOCATION);

        //matches '/location'
        matcher.addURI(authority, FestivalContract.PATH_LOCATION, LOCATIONS);

        return matcher;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch(match){

            case EVENT_WITH_VENUE:
                return FestivalContract.EventEntry.CONTENT_TYPE;
            case VENUE_WITH_LOCATION:
                return FestivalContract.VenueEntry.CONTENT_TYPE;

            case EVENTS:
                return FestivalContract.EventEntry.CONTENT_TYPE;
            case EVENT:
                return FestivalContract.EventEntry.CONTENT_ITEM_TYPE;

            case VENUES:
                return FestivalContract.VenueEntry.CONTENT_TYPE;
            case VENUE:
                return FestivalContract.VenueEntry.CONTENT_ITEM_TYPE;

            case LOCATIONS:
                return FestivalContract.LocationEntry.CONTENT_TYPE;
            case LOCATION:
                return FestivalContract.LocationEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder){
        Cursor retCursor;
        Log.v(LOG_TAG, "Query: Uri Matched to " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                FestivalContract.EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case EVENT_WITH_VENUE:
            {
                retCursor = getEventByVenue(uri, projection, sortOrder);
                break;
            }


            case VENUES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FestivalContract.VenueEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }

            case VENUE_WITH_LOCATION:
            {
                retCursor = getVenueByLocation(uri, projection, sortOrder);
                break;
            }

            case LOCATIONS:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FestivalContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Watches for changes to the uri and notifies cursor when it happens
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    private Cursor getEventByVenue(Uri uri, String[] projection, String sortOrder){
        String venue = FestivalContract.EventEntry.getVenueFromUri(uri);

        String[] selectionArgs;
        String selection;

        //selection = null;
        //selectionArgs = null;
        selection = sVenueSettingSelection;
        selectionArgs = new String[]{venue};


        return sEventByVenueSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
                );
    }

    private Cursor getVenueByLocation(Uri uri, String[] projection, String sortOrder){
        String location = FestivalContract.VenueEntry.getLocationFromUri(uri);

        String[] selectionArgs;
        String selection;


        selection = sLocationSettingSelection;
        selectionArgs = new String[]{location};


        return sVenueByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case EVENTS: {
                long _id = db.insert(FestivalContract.EventEntry.TABLE_NAME, null, values);
                if (_id > 0) returnUri = FestivalContract.EventEntry.buildEventUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case VENUES: {
                long _id = db.insert(FestivalContract.VenueEntry.TABLE_NAME, null, values);
                if (_id > 0) returnUri = FestivalContract.VenueEntry.buildVenueUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case LOCATIONS: {
                long _id = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, values);
                if (_id > 0) returnUri = FestivalContract.LocationEntry.buildLocationUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection) selection = "1";

        switch(match){
            case EVENTS: {
                rowsUpdated = db.update(FestivalContract.EventEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case VENUES: {
                rowsUpdated = db.update(FestivalContract.VenueEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LOCATIONS: {
                rowsUpdated = db.update(FestivalContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
            }
        }

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);

        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //This makes delete all rows return the number of rows deleted
        if (null == selection ) selection = "1";

        switch (match) {
            case EVENTS: {
                rowsDeleted = db.delete(FestivalContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case VENUES: {
                rowsDeleted = db.delete(FestivalContract.VenueEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case LOCATIONS: {
                rowsDeleted = db.delete(FestivalContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();

        return rowsDeleted;
    }
}
