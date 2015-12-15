package com.example.android.nextfest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FestivalDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "festival.db";

    public FestivalDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + FestivalContract.EventEntry.TABLE_NAME + " (" +
                FestivalContract.EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FestivalContract.EventEntry.COLUMN_VENUE_KEY + " INTEGER NOT NULL, " +
                FestivalContract.EventEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                FestivalContract.EventEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                FestivalContract.EventEntry.COLUMN_END_DATE + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + FestivalContract.EventEntry.COLUMN_VENUE_KEY + ") REFERENCES " +
                FestivalContract.VenueEntry.TABLE_NAME + " (" + FestivalContract.VenueEntry._ID + ")," +

                //Each venue can only have 1 entry per event date
                "UNIQUE ("  + FestivalContract.EventEntry.COLUMN_VENUE_KEY + ", " +
                FestivalContract.EventEntry.COLUMN_START_DATE + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_VENUE_TABLE = "CREATE TABLE " + FestivalContract.VenueEntry.TABLE_NAME + " (" +
                FestivalContract.VenueEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FestivalContract.VenueEntry.COLUMN_LOCATION_KEY + " INTEGER NOT NULL, " +
                FestivalContract.VenueEntry.COLUMN_VENUE_NAME + " TEXT NOT NULL," +

                " FOREIGN KEY (" + FestivalContract.VenueEntry.COLUMN_LOCATION_KEY + ") REFERENCES " +
                FestivalContract.LocationEntry.TABLE_NAME + " (" + FestivalContract.LocationEntry._ID + ")" +
                ");";


        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + FestivalContract.LocationEntry.TABLE_NAME + " (" +
                FestivalContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FestivalContract.LocationEntry.COLUMN_CITY + " TEXT UNIQUE NOT NULL," +
                FestivalContract.LocationEntry.COLUMN_COUNTRY + " TEXT NOT NULL," +
                FestivalContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL," +
                FestivalContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VENUE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FestivalContract.EventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FestivalContract.VenueEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FestivalContract.LocationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
