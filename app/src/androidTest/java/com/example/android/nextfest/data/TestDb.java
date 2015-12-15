package com.example.android.nextfest.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        mContext.deleteDatabase(FestivalDbHelper.DATABASE_NAME);
    }

    //This method is called at the beginning of a test
    public void setUp(){deleteDatabase();}

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FestivalContract.EventEntry.TABLE_NAME);
        tableNameHashSet.add(FestivalContract.VenueEntry.TABLE_NAME);
        tableNameHashSet.add(FestivalContract.LocationEntry.TABLE_NAME);

        //TEST: New Database is created properly
        mContext.deleteDatabase(FestivalDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FestivalDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //TEST: New Database returns cursor
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        //TEST: New Database returns all tables
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Database was created without multiple tables", tableNameHashSet.isEmpty());

        //TEST: New Database allows access to tables
        c = db.rawQuery("PRAGMA table_info(" + FestivalContract.LocationEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());

        //TEST: New Database Location Table contains all columns
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(FestivalContract.LocationEntry._ID);
        locationColumnHashSet.add(FestivalContract.LocationEntry.COLUMN_CITY);
        locationColumnHashSet.add(FestivalContract.LocationEntry.COLUMN_COUNTRY);
        locationColumnHashSet.add(FestivalContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(FestivalContract.LocationEntry.COLUMN_COORD_LONG);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);

        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns", locationColumnHashSet.isEmpty());
        db.close();

    }

    public void testEventTable(){
        long venueRowID = insertVenue();

        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createEventValues(venueRowID);

        //TEST: Inserted to database succesfully
        long eventRowID = db.insert(FestivalContract.EventEntry.TABLE_NAME, null, testValues);
        assertTrue(eventRowID != -1);

        //TEST: Inserted record is returned from the database
        Cursor cursor = db.query(
                FestivalContract.EventEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No records returned from database", cursor.moveToFirst());

        //TEST: Validate record
        //TestUtilities.validateCurrentRecord("Error: Event Query Validation Failed", cursor, testValues);

        //TEST: Verify that it doesn't return more than one record
        assertFalse("Error: More than one record returned from event entry", cursor.moveToNext());

        cursor.close();
        dbHelper.close();


    }

    public void testVenueTable(){ insertVenue(); }

    public void testLocationTable(){ insertLocation(); }

    public long insertVenue() {
        long locationRowID = insertLocation();

        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createVenueValues(locationRowID);

        long venueRowId = db.insert(FestivalContract.VenueEntry.TABLE_NAME, null, testValues);

        Cursor cursor = db.query(
                FestivalContract.LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No records returned from venue query", cursor.moveToFirst());

        //TEST: Validate location entries
        //TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        //TEST: Validate only one row was created
        assertFalse("Error: More than one record return from location query", cursor.moveToNext());

        cursor.close();
        db.close();

        return venueRowId;

    }

    public long insertLocation() {
        FestivalDbHelper dbHelper = new FestivalDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLocationValues();

        //TEST: Verify that location entries enter into the table
        long locationRowId;
        locationRowId = db.insert(FestivalContract.LocationEntry.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        //TEST: Verify that location table returns rows to cursor
        Cursor cursor = db.query(
                FestivalContract.LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No records returned from location query", cursor.moveToFirst());

        //TEST: Validate location entries
        //TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        //TEST: Validate only one row was created
        assertFalse("Error: More than one record return from location query", cursor.moveToNext());

        cursor.close();
        db.close();

        return locationRowId;
    }


}
