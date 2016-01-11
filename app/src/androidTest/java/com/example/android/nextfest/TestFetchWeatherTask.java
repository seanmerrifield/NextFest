package com.example.android.nextfest;


import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.nextfest.data.FestivalContract;

public class TestFetchWeatherTask extends AndroidTestCase {

    static final String ADD_LOCATION_CITY = "San Francisco";
    static final String ADD_LOCATION_COUNTRY = "USA";
    static final double ADD_LOCATION_LAT = 34.425833;
    static final double ADD_LOCATION_LON = -119.714167;

    @TargetApi(11)
    public void testLocation(){
        getContext().getContentResolver().delete(FestivalContract.LocationEntry.CONTENT_URI,
                FestivalContract.LocationEntry.COLUMN_CITY + " = ?",
                new String[]{ADD_LOCATION_CITY});

        FetchFestivalTask task = new FetchFestivalTask(getContext(), null);
        long locationId = task.addLocation(ADD_LOCATION_CITY, ADD_LOCATION_COUNTRY, ADD_LOCATION_LAT, ADD_LOCATION_LON);

        assertFalse("Error: addLocation returned an invalid ID on insert", locationId == -1);

        for ( int i = 0; i < 2; i++) {
            Cursor locationCursor = getContext().getContentResolver().query(
                    FestivalContract.LocationEntry.CONTENT_URI,
                    new String[]{
                            FestivalContract.LocationEntry._ID,
                            FestivalContract.LocationEntry.COLUMN_CITY,
                            FestivalContract.LocationEntry.COLUMN_COUNTRY,
                            FestivalContract.LocationEntry.COLUMN_COORD_LAT,
                            FestivalContract.LocationEntry.COLUMN_COORD_LONG
                    },
                    FestivalContract.LocationEntry.COLUMN_CITY + " = ?",
                    new String[]{ADD_LOCATION_CITY},
                    null);
            boolean test = locationCursor.moveToFirst();
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value from addLocation",
                        locationCursor.getLong(0), locationId);
                assertEquals("Error: the queried value of location setting is incorrect",
                        locationCursor.getString(1), ADD_LOCATION_CITY);
                assertEquals("Error: the queried value of location city is incorrect",
                        locationCursor.getString(2), ADD_LOCATION_COUNTRY);
                assertEquals("Error: the queried value of latitude is incorrect",
                        locationCursor.getDouble(3), ADD_LOCATION_LAT);
                assertEquals("Error: the queried value of longitude is incorrect",
                        locationCursor.getDouble(4), ADD_LOCATION_LON);
            } else {
                fail("Error: the id that was used to query returned an empty cursor");
            }

            //Test: Make sure only one row was returned from query
            assertFalse("Error: there should be only one record returned from a location query", locationCursor.moveToNext());

            //Add location again
            long newLocationId = task.addLocation(ADD_LOCATION_CITY, ADD_LOCATION_COUNTRY, ADD_LOCATION_LAT, ADD_LOCATION_LON);

            assertEquals("Error: inserting a location again should return the same ID", locationId, newLocationId);
        }
        getContext().getContentResolver().delete(FestivalContract.LocationEntry.CONTENT_URI,
                FestivalContract.LocationEntry.COLUMN_CITY + " = ?", new String[]{ADD_LOCATION_CITY});

        getContext().getContentResolver().acquireContentProviderClient(FestivalContract.LocationEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
