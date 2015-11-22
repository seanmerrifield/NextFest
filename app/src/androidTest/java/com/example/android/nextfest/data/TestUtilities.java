package com.example.android.nextfest.data;

import android.content.ContentValues;
import android.test.AndroidTestCase;


public class TestUtilities extends AndroidTestCase{

    static final String CITY = "Amsterdam";
    static final String COUNTRY = "Netherlands";
    static final String VENUE_NAME = "Paradiso";

    static final long TEST_START_DATE = 1451001600000L; //  12/25/2015
    static final long TEST_END_DATE = 1451088000000L; //    12/26/2015

    static ContentValues createAmsterdamLocationValues(){
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.LocationEntry.COLUMN_COUNTRY, COUNTRY);
        testValues.put(FestivalContract.LocationEntry.COLUMN_CITY, CITY);
        testValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LAT, 52.37403);
        testValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LONG, 4.8896900);
        return testValues;
    };

    static ContentValues createEventValues(long venueRowID){
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.EventEntry.COLUMN_EVENT_NAME, "Above & Beyond at Paradiso");
        testValues.put(FestivalContract.EventEntry.COLUMN_VENUE_KEY, venueRowID);
        testValues.put(FestivalContract.EventEntry.COLUMN_START_DATE, TEST_START_DATE);
        testValues.put(FestivalContract.EventEntry.COLUMN_END_DATE, TEST_END_DATE);
        return testValues;
    }

    static ContentValues createVenueValues(long locationRowID) {
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.VenueEntry.COLUMN_VENUE_NAME, VENUE_NAME);
        testValues.put(FestivalContract.VenueEntry.COLUMN_LOCATION_KEY, locationRowID);
        return testValues;
    }

};
