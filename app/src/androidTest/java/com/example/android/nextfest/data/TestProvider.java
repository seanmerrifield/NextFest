package com.example.android.nextfest.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;


public class TestProvider extends AndroidTestCase{

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete( FestivalContract.EventEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete( FestivalContract.VenueEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete( FestivalContract.LocationEntry.CONTENT_URI, null, null);

        //Test: Verify all data was removed from Event Table
        Cursor cursor = mContext.getContentResolver().query(FestivalContract.EventEntry.CONTENT_URI, null, null, null, null);
        assertEquals("Error: Records not deleted from Event Table during delete", 0, cursor.getCount());
        cursor.close();

        //Test: Verify all data was removed from Event Table
        cursor = mContext.getContentResolver().query(FestivalContract.VenueEntry.CONTENT_URI, null, null, null, null);
        assertEquals("Error: Records not deleted from Event Table during delete", 0, cursor.getCount());
        cursor.close();

        //Test: Verify all data was removed from Location Table
        cursor = mContext.getContentResolver().query(FestivalContract.LocationEntry.CONTENT_URI, null, null, null, null);
        assertEquals("Error: Records not deleted from Location Table during delete", 0, cursor.getCount());
        cursor.close();


    }

    public void deleteAllRecordsFromDB() {
        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FestivalContract.EventEntry.TABLE_NAME, null, null);
        db.delete(FestivalContract.VenueEntry.TABLE_NAME, null, null);
        db.delete(FestivalContract.LocationEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
        //deleteAllRecordsFromDB();
    }


    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                FestivalProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: FestivalProvider registered with authorrity: " + providerInfo.authority +
                            " instead of authority: " + FestivalContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FestivalContract.CONTENT_AUTHORITY);
        }

        catch(PackageManager.NameNotFoundException e){
            assertTrue("Error: FestivalProvider not registered at " + mContext.getPackageName(), false);
        }
    }


    public void testGetType(){

        // Test: event Type
        // content://com.example.android.nextfest/event
        String type = mContext.getContentResolver().getType(FestivalContract.EventEntry.CONTENT_URI);
        assertEquals("Error: the EventEntry CONTENT_URI should return EventEntry.CONTENT_URI",
                FestivalContract.EventEntry.CONTENT_TYPE, type);

        // Test: Event with venue Type
        // content://com.example.android.nextfest/event/paradiso
        String testVenue = "paradiso";
        type = mContext.getContentResolver().getType(FestivalContract.EventEntry.buildEventVenue(testVenue));
        assertEquals("Error: the EventEntry CONTENT_URI with locaton should return EventEntry.CONTENT_URI",
                FestivalContract.EventEntry.CONTENT_TYPE, type);

        // Test: Venue Type
        // content://com.example.android.nextfest/venue
        type = mContext.getContentResolver().getType(FestivalContract.VenueEntry.CONTENT_URI);
        assertEquals("Error: the VenueEntry CONTENT_URI should return VenueEntry.CONTENT_URI",
                FestivalContract.VenueEntry.CONTENT_TYPE, type);

        // Test: Venue with Location Type
        // content://com.example.android.nextfest/event/paradiso
        String testLocation = "Amsterdam";
        type = mContext.getContentResolver().getType(FestivalContract.VenueEntry.buildVenueLocation(testVenue));
        assertEquals("Error: the VenueEntry CONTENT_URI should return VenueEntry.CONTENT_URI",
                FestivalContract.VenueEntry.CONTENT_TYPE, type);

        // Test: Location Type
        // content://com.example.android.nextfest/location
        type = mContext.getContentResolver().getType(FestivalContract.LocationEntry.CONTENT_URI);
        assertEquals("Error: the LOCATIONEntry CONTENT_URI should return LocationEntry.CONTENT_URI",
                FestivalContract.LocationEntry.CONTENT_TYPE, type);


    }


    public void testBasicEventQuery() {
        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create Location Values
        ContentValues locationValues = TestUtilities.createLocationValues();
        long locationRowId = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, locationValues);

        // Create Venue Values
        ContentValues venueValues = TestUtilities.createVenueValues(locationRowId);
        long venueRowId = db.insert(FestivalContract.VenueEntry.TABLE_NAME, null, venueValues);

        // Create Event values
        ContentValues eventValues = TestUtilities.createEventValues(venueRowId);
        long eventRowId;
        eventRowId = db.insert(FestivalContract.EventEntry.TABLE_NAME, null, eventValues);

        // Test: Event Values Inserted into the table
        assertTrue("Error: Failure to insert Event Values", eventRowId != -1);

        db.close();

        Cursor eventCursor = mContext.getContentResolver().query(
                FestivalContract.EventEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicEventQuery", eventCursor, eventValues);

    }

    public void testBasicVenueQueries() {
        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Create Loccation Values
        ContentValues locationValues = TestUtilities.createLocationValues();
        long locationRowId = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, locationValues);

        //Create Venue Values
        ContentValues venueValues = TestUtilities.createVenueValues(locationRowId);
        long venueRowId = db.insert(FestivalContract.VenueEntry.TABLE_NAME, null, venueValues);

        Cursor venueCursor = mContext.getContentResolver().query(
                FestivalContract.VenueEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicVenueQueries, venue query", venueCursor, venueValues);

        db.close();
    }

    public void testBasicLocationQueries() {
        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLocationValues();
        long locationRowId = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, testValues);

        Cursor locationCursor = mContext.getContentResolver().query(
                FestivalContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);

        db.close();
    }


    public void testUpdateLocation(){
        ContentValues locationValues = TestUtilities.createLocationValues();

        Uri locationUri = mContext.getContentResolver().insert(FestivalContract.LocationEntry.CONTENT_URI, locationValues);
        long locationRowId = ContentUris.parseId(locationUri);

        //Test: Verify Location Insertion Was Successful
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        //Create New Values
        ContentValues updatedValues = new ContentValues(locationValues);
        updatedValues.put(FestivalContract.LocationEntry._ID, locationRowId);
        updatedValues.put(FestivalContract.LocationEntry.COLUMN_CITY, "London");

        Cursor locationCursor = mContext.getContentResolver().query(FestivalContract.LocationEntry.CONTENT_URI, null, null, null, null);

        //Setup Content Observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        //Update values in database
        int count = mContext.getContentResolver().update(
                FestivalContract.LocationEntry.CONTENT_URI, updatedValues, FestivalContract.LocationEntry._ID + "= ?",
                new String[] {Long.toString(locationRowId)});

        //Test: Make sure update was successfull
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        //Retrieve updated row
        Cursor cursor = mContext.getContentResolver().query(
                FestivalContract.LocationEntry.CONTENT_URI,
                null,
                FestivalContract.LocationEntry._ID  + "=" + locationRowId,
                null,
                null
        );

        //Test: Verify updated row matches updated values
        TestUtilities.validateCursor("testUpdateLocation. Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {

        /// LOCATION DATA SETUP ///

        ContentValues locationValues = TestUtilities.createLocationValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FestivalContract.LocationEntry.CONTENT_URI, true, tco);

        //Add new data to location table
        Uri locationUri = mContext.getContentResolver().insert(FestivalContract.LocationEntry.CONTENT_URI, locationValues);

        //Test: Checks if content observer was notified of the change
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        //Test: Verify that the location data was inserted successfully
        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(FestivalContract.LocationEntry.CONTENT_URI, null, null, null, null);
        //Test: Verify that the location data was input correctly
        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.", cursor, locationValues);

        /// VENUE DATA SETUP ///

        //Create venue values and register content observer
        ContentValues venueValues = TestUtilities.createVenueValues(locationRowId);
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FestivalContract.VenueEntry.CONTENT_URI, true, tco);

        //Add new data to the Venue table
        Uri venueUri = mContext.getContentResolver().insert(FestivalContract.VenueEntry.CONTENT_URI, venueValues);
        assertTrue(venueUri != null);

        //Test: Checks if content observer was notified of the change
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long venueRowId = ContentUris.parseId(venueUri);

        //Test: Verify that the Venue data was inserted successfully
        assertTrue(venueRowId != -1);

        Cursor venueCursor = mContext.getContentResolver().query(FestivalContract.VenueEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testInsertReadProvider. Error validating VenueEntry insert.", venueCursor, venueValues);


        /// EVENT DATA SETUP ///

        //Create Event values and register content observer
        ContentValues eventValues = TestUtilities.createEventValues(venueRowId);
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FestivalContract.EventEntry.CONTENT_URI, true, tco);

        //Add new data to the Event table
        Uri eventUri = mContext.getContentResolver().insert(FestivalContract.EventEntry.CONTENT_URI, eventValues);
        assertTrue(eventUri != null);

        //Test: Checks if content obsrver was notified of the change
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long eventRowId = ContentUris.parseId(eventUri);

        //Test: Verify that the Event data was inserted successfully
        assertTrue(eventRowId != -1);

        Cursor eventCursor = mContext.getContentResolver().query(FestivalContract.EventEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testInsertReadProvider. Error Validating EventEntry insert.", eventCursor, eventValues);

        //Combine venue values in event values to check joined Event and Venue table data
        eventValues.putAll(venueValues);

        //Query joining Event and Venue table data
        eventCursor = mContext.getContentResolver().query(
                FestivalContract.EventEntry.buildEventVenue(TestUtilities.VENUE_NAME),
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("testInsertReadProvider. Error Validating joined Event and Venue insert.", eventCursor, eventValues);


        /*
        //Combine locationValues in venueValues to check joined Venue and Location table data
        venueValues.putAll(locationValues);

        //Query joining Venue and Location data
        venueCursor = mContext.getContentResolver().query(
                FestivalContract.VenueEntry.buildVenueLocation(TestUtilities.CITY),
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("testInsertReadProvider. Error Validating joined Venue and Location insert.", venueCursor, venueValues);
    */

    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 5;
    static ContentValues[] createBulkInsertVenueValues(long locationRowId){
        ContentValues[] venuesArray = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        String[] venueNames = {"Paradiso", "Heineken Music Hall", "Markt Kantine", "Studio 80", "Flower Bar"};

        for (int i=0; i < BULK_INSERT_RECORDS_TO_INSERT; i++){
            ContentValues venueValues = new ContentValues();
            venueValues.put(FestivalContract.VenueEntry.COLUMN_LOCATION_KEY, locationRowId);
            venueValues.put(FestivalContract.VenueEntry.COLUMN_VENUE_NAME, venueNames[i]);

            venuesArray[i] = venueValues;
        }

        return venuesArray;
    }

    static ContentValues[] createBulkInsertEventValues(Cursor venueCursor){
        ContentValues[] eventsArray = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        String[] eventNames = {"XX at Paradiso", "XX at Heineken Music Hall", "XX at Markt Kantine", "XX at Studio 80", "XX at Flower Bar"};


        venueCursor.moveToFirst();
        for (int i=0; i < BULK_INSERT_RECORDS_TO_INSERT; i++){

            ContentValues eventValues = new ContentValues();
            //Get venueId from Venue Cursor
            eventValues.put(FestivalContract.EventEntry.COLUMN_VENUE_KEY, venueCursor.getInt(0));
            eventValues.put(FestivalContract.EventEntry.COLUMN_EVENT_NAME, eventNames[i]);
            eventValues.put(FestivalContract.EventEntry.COLUMN_START_DATE, TestUtilities.TEST_START_DATE);
            eventValues.put(FestivalContract.EventEntry.COLUMN_END_DATE, TestUtilities.TEST_END_DATE);

            eventsArray[i] = eventValues;

            venueCursor.moveToNext();
        }

        return eventsArray;
    }

    public void testBulkInsert(){
        //Create location data
        ContentValues locationValues = TestUtilities.createLocationValues();
        Uri locationUri = mContext.getContentResolver().insert(FestivalContract.LocationEntry.CONTENT_URI, locationValues);
        long locationRowId = ContentUris.parseId(locationUri);

        //Test: Verify insertion was successful
        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(FestivalContract.LocationEntry.CONTENT_URI, null, null, null, null);
        //Test: Verify data has been input correctly
        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.", cursor, locationValues);



        //// TEST VENUE BULK INSERT ////

        ContentValues[] venueBulkInsertValues = createBulkInsertVenueValues(locationRowId);
        int venueInsertCount = mContext.getContentResolver().bulkInsert(FestivalContract.VenueEntry.CONTENT_URI, venueBulkInsertValues);

        //Test: Verify inserted # of rows matches expected value
        assertEquals(venueInsertCount, BULK_INSERT_RECORDS_TO_INSERT);

        cursor = mContext.getContentResolver().query(FestivalContract.VenueEntry.CONTENT_URI, null, null, null, null);

        //Test: Verify cursor has # of rows that matches expected value
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();

        for(int i =0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()){
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating VenueEntry " + i,
                    cursor, venueBulkInsertValues[i]);
        }


        //// TEST EVENT BULK INSERT ////

        ContentValues[] eventBulkInsertValues = createBulkInsertEventValues(cursor);

        TestUtilities.TestContentObserver eventObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FestivalContract.EventEntry.CONTENT_URI, true, eventObserver);


        int eventInsertCount = mContext.getContentResolver().bulkInsert(FestivalContract.EventEntry.CONTENT_URI, eventBulkInsertValues);

        //Test: Verify content observer notices change
        eventObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(eventObserver);

        //Test: Verify inserted # of rows matches expected value
        assertEquals(eventInsertCount, BULK_INSERT_RECORDS_TO_INSERT);


        cursor = mContext.getContentResolver().query(FestivalContract.EventEntry.CONTENT_URI, null, null, null, null);

        //Test: Verify cursor has # of rows that matches expected value
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for(int i =0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()){
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating EventEntry " + i,
                    cursor, eventBulkInsertValues[i]);
        }

        cursor.close();
    }

}
