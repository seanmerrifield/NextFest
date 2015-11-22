package com.example.android.nextfest.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        mContext.deleteDatabase(FestivalDbHelper.DATABASE_NAME);
    }

    //This method is called at the beginning of a test
    public void setUp(){deleteDatabase()};

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FestivalContract.EventEntry.TABLE_NAME);
        tableNameHashSet.add(FestivalContract.VenueEntry.TABLE_NAME);
        tableNameHashSet.add(FestivalContract.LocationEntry.TABLE_NAME);

        mContext.deleteDatabase(FestivalDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FestivalDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());


        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Database was created without multiple tables", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + FestivalContract.LocationEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());

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




}
