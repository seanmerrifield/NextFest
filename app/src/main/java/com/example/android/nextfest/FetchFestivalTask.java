package com.example.android.nextfest;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.nextfest.data.FestivalContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class FetchFestivalTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchFestivalTask.class.getSimpleName();

    private final ArrayAdapter<String> mFestivalAdapter;
    private final Context mContext;

    //Initialize instance variables in constructor
    public FetchFestivalTask(Context context, ArrayAdapter<String> festivalAdapter){
        mContext = context;
        mFestivalAdapter = festivalAdapter;
    }


    private Calendar formatDateString(String dateStr, String format){

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        Calendar cal = new GregorianCalendar();

        try {
            cal.setTime(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cal;
    }

    long addLocation(String cityName, String countryName, double lat, double lon){
        long locationId;

        //First check if it already exists in the database
        Cursor locationCursor = mContext.getContentResolver().query(
                FestivalContract.LocationEntry.CONTENT_URI,
                new String[]{FestivalContract.LocationEntry._ID},
                FestivalContract.LocationEntry.COLUMN_CITY + " = ?",
                new String[]{cityName},
                null);

        //If it exists, set locationId to returned Id
        if(locationCursor.moveToFirst()){
            int locationIdIndex = locationCursor.getColumnIndex(FestivalContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        }
        else{
            ContentValues locationValues = new ContentValues();

            locationValues.put(FestivalContract.LocationEntry.COLUMN_CITY, cityName);
            locationValues.put(FestivalContract.LocationEntry.COLUMN_COUNTRY, countryName);
            locationValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(FestivalContract.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri insertedUri = mContext.getContentResolver().insert(FestivalContract.LocationEntry.CONTENT_URI, locationValues);

            locationId = ContentUris.parseId(insertedUri);
        }
        locationCursor.close();

        return locationId;
    }

    long addVenue(String venueName, long locationId){
        long venueId;

        //First check if it already exists in the database
        Cursor venueCursor = mContext.getContentResolver().query(
                FestivalContract.VenueEntry.CONTENT_URI,
                new String[]{FestivalContract.VenueEntry._ID},
                FestivalContract.VenueEntry.COLUMN_VENUE_NAME + " = ?",
                new String[]{venueName},
                null);

        //If it exists, set locationId to returned Id
        if(venueCursor.moveToFirst()){
            int venueIdIndex = venueCursor.getColumnIndex(FestivalContract.VenueEntry._ID);
            venueId = venueCursor.getLong(venueIdIndex);
        }
        else{
            ContentValues venueValues = new ContentValues();

            venueValues.put(FestivalContract.VenueEntry.COLUMN_VENUE_NAME, venueName);
            venueValues.put(FestivalContract.VenueEntry.COLUMN_LOCATION_KEY, locationId);


            Uri insertedUri = mContext.getContentResolver().insert(FestivalContract.VenueEntry.CONTENT_URI, venueValues);

            venueId = ContentUris.parseId(insertedUri);
        }
        venueCursor.close();

        return venueId;
    }

    String[] parseLocationString(String locationString){
        String delims = "[ ,]";
        return locationString.split(delims);
    }

    public String[] getFestivalDataFromJson(String festivalJsonStr){

        final String POPULARITY_KEY = "popularity";
        final String VENUE_KEY = "venue";
        final String LOCATION_KEY = "location";
        final String LATITUDE_KEY = "lat";
        final String LONGITUDE_KEY = "lng";
        final String CITY_KEY = "city";
        final String EVENT_TYPE_KEY = "type";
        final String NAME_KEY = "displayName";
        final String START_TIME_KEY = "start";
        final String DATE_KEY = "date";
        final String TIME_KEY = "time";


        final String RESULTS_PAGE_KEY = "resultsPage";
        final String RESULTS_KEY = "results";
        final String EVENT_KEY = "event";
        final String PERFORMANCE_KEY = "performance";

        try {

            JSONObject eventsJSON = new JSONObject(festivalJsonStr);
            eventsJSON = eventsJSON.getJSONObject(RESULTS_PAGE_KEY).getJSONObject(RESULTS_KEY);

            //Gets array of events at location
            JSONArray eventsArray = eventsJSON.getJSONArray(EVENT_KEY);

            //All event data will be placed here
            Vector<ContentValues> cVVector = new Vector<ContentValues>(eventsArray.length());

            String[] resultStrs = new String[eventsArray.length()];

            for (int i = 0; i < eventsArray.length(); i++){

                JSONObject eventJson = eventsArray.getJSONObject(i);
                JSONObject venueJson = eventJson.getJSONObject(VENUE_KEY);
                JSONObject locationJson = eventJson.getJSONObject(LOCATION_KEY);
                JSONObject dateJson = eventJson.getJSONObject(START_TIME_KEY);

                JSONArray performersArray = eventJson.getJSONArray(PERFORMANCE_KEY);

                JSONObject headlinerJSON;
                String headliner;

                //EVENT DATA

                //If there are performers, find headliner
                if (performersArray.isNull(0)) {
                    headliner = "TBD";
                }
                else {
                    //Assumes first object in array is the headliner
                    headlinerJSON = performersArray.getJSONObject(0);
                    headliner = headlinerJSON.getString(NAME_KEY);
                }

                String eventType = eventJson.getString(EVENT_TYPE_KEY);
                String eventName = eventJson.getString(NAME_KEY);
                double popularity = eventJson.getDouble(POPULARITY_KEY);
                String dateStr = dateJson.getString(DATE_KEY);
                Calendar cal = formatDateString(dateStr, "yyyy-MM-dd");

                //VENUE DATA
                String venue = venueJson.getString(NAME_KEY);

                //LOCATION DATA
                String locationStr = locationJson.getString(CITY_KEY);
                String[] locationData = parseLocationString(locationStr);
                String city = locationData[0];
                String country = locationData[1];
                Double latitude = locationJson.getDouble(LATITUDE_KEY);
                Double longitude =locationJson.getDouble(LONGITUDE_KEY);


                long locationId = addLocation(city, country, latitude, longitude);
                long venueId = addVenue(venue, locationId);


                ContentValues eventValues = new ContentValues();


                eventValues.put(FestivalContract.EventEntry.COLUMN_VENUE_KEY, venueId);
                eventValues.put(FestivalContract.EventEntry.COLUMN_EVENT_NAME, eventName);
                eventValues.put(FestivalContract.EventEntry.COLUMN_START_DATE, dateStr);
                //Need to fix end date string
                eventValues.put(FestivalContract.EventEntry.COLUMN_END_DATE, dateStr);

                cVVector.add(eventValues);

                resultStrs[i] = dateStr + " - " + headliner + " - " + venue;

            }

            long insertCount;
            if (cVVector.size() > 0){
                ContentValues[] cVArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cVArray);
                insertCount = mContext.getContentResolver().bulkInsert(FestivalContract.EventEntry.CONTENT_URI, cVArray);
            }
            else{
                insertCount = 0;
            }

            Log.d(LOG_TAG, insertCount + " out of " + cVVector.size() + " successfully inserted.");


            return resultStrs;


        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    };

   @Override
    protected String[] doInBackground(String... params) {

       if (params.length == 0){
           return null;
       }

       //Assumes first parameter that's passed into FetchFestivalTask is the location key
       String locationQuery = params[0];


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON String that will be obtained from get request
        String festivalJsonStr = null;

       //query parameters
        String apikey = "7xVXPkI8sUxkF8wb";
       try {
           //URL = "http://api.songkick.com/api/3.0/metro_areas/31366/calendar.json?apikey=7xVXPkI8sUxkF8wb";
           final String FESTIVAL_BASE_URL = "http://api.songkick.com/api/3.0";
           final String AREA_PATH = "metro_areas";
           final String JSON_PATH = "calendar.json";
           final String API_PARAM = "apikey";

           //Build up Uri to access API
           Uri builtUri = Uri.parse(FESTIVAL_BASE_URL).buildUpon()
                   .appendPath(AREA_PATH)
                   .appendPath(locationQuery)
                   .appendPath(JSON_PATH)
                   .appendQueryParameter(API_PARAM, apikey)
                   .build();

           URL url = new URL(builtUri.toString());

           urlConnection = (HttpURLConnection) url.openConnection();
           urlConnection.setRequestMethod("GET");
           urlConnection.connect();

           InputStream inputStream = urlConnection.getInputStream();
           StringBuffer buffer = new StringBuffer();
           if (inputStream == null) {
               return null;
           }

           //Buffered reader provides an efficient way of reading the retrieved data
           reader = new BufferedReader(new InputStreamReader(inputStream));
           String line;
           while((line = reader.readLine()) != null){
               buffer.append(line + "\n");
           }

           if (buffer.length() == 0){
               return null;
           }

           festivalJsonStr = buffer.toString();


       }
       catch (IOException e){
           Log.e(LOG_TAG, "Error ", e);
           return null;
       }

       //Close all connections and the reader
       finally{
           if (urlConnection != null){
           urlConnection.disconnect();
           }

           if (reader != null) {
               try {
                   reader.close();
               } catch (final IOException e) {
                   Log.e(LOG_TAG, "Error closing stream", e);
               }
           }
       }

       return getFestivalDataFromJson(festivalJsonStr);

   }


    @Override
    protected void onPostExecute(String[] result) {
        if (result != null){
            mFestivalAdapter.clear();
            for (String eventStr : result){
                mFestivalAdapter.add(eventStr);
            }
        }
    }

}
