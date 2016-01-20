package com.example.android.nextfest.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.nextfest.utils.PollingCheck;

import java.util.Map;
import java.util.Set;


public class TestUtilities extends AndroidTestCase {

    static final String CITY = "Amsterdam";
    static final String COUNTRY = "Netherlands";
    static final Long LOCATION_SETTING = 31366L;
    static final Double LATITUDE = 52.3740;
    static final Double LONGITUDE = 4.88969;
    static final String VENUE_NAME = "Paradiso";

    static final String HEADLINER = "Above & Beyond";
    static final long TEST_DATE = 1451001600000L; //  12/25/2015
    static final int TEST_TIME = 21; //    9 pm


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            Log.d("Debug Log. ", "Column " + columnName + ": Expected Value is " + expectedValue + " and Cursor value is " + valueCursor.getString(idx));
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createLocationValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.LocationEntry.COLUMN_LOCATION_SETTING, LOCATION_SETTING);
        testValues.put(FestivalContract.LocationEntry.COLUMN_COUNTRY, COUNTRY);
        testValues.put(FestivalContract.LocationEntry.COLUMN_CITY, CITY);
        testValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LAT, 52.3740);
        testValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LONG, 4.88969);
        return testValues;
    }

    static ContentValues createEventValues(long venueRowID) {
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.EventEntry.COLUMN_EVENT_NAME, "Above & Beyond at Paradiso");
        testValues.put(FestivalContract.EventEntry.COLUMN_VENUE_KEY, venueRowID);
        testValues.put(FestivalContract.EventEntry.COLUMN_HEADLINER, HEADLINER);
        testValues.put(FestivalContract.EventEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(FestivalContract.EventEntry.COLUMN_TIME, TEST_TIME);
        return testValues;
    }

    static ContentValues createVenueValues(long locationRowID) {
        ContentValues testValues = new ContentValues();
        testValues.put(FestivalContract.VenueEntry.COLUMN_VENUE_NAME, VENUE_NAME);
        testValues.put(FestivalContract.VenueEntry.COLUMN_LOCATION_KEY, locationRowID);
        return testValues;
    }

    static ContentValues createEventWithVenueValues(long locationRowID){
        ContentValues testValues = createEventValues(locationRowID);
        testValues.put(FestivalContract.VenueEntry.COLUMN_VENUE_NAME, VENUE_NAME);
        testValues.put(FestivalContract.VenueEntry.COLUMN_LOCATION_KEY, locationRowID);
        return testValues;
    }

/*
    static long insertLocationValues(Context context) {

        FestivalDbHelper dbHelper = new FestivalDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLocationValues();

        long locationRowId;
        locationRowId = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Location Values", locationRowId != -1);

        db.close();

        return locationRowId;

    }

    static long insertVenueValues(SQLiteDatabase db, long locationRowId) {

        ContentValues testValues = TestUtilities.createVenueValues(locationRowId);

        long venueRowId;
        venueRowId = db.insert(FestivalContract.VenueEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Venue Values", venueRowId != -1);


        return venueRowId;
    }
    */

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver(){
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) { onChange(selfChange, null);}

        @Override
        public void onChange(boolean selfChange, Uri uri) {mContentChanged = true;}

        public void waitForNotificationOrFail(){
            new PollingCheck(5000) {
                @Override
                protected boolean check() { return mContentChanged; }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver(){
        return TestContentObserver.getTestContentObserver();
    }
}

