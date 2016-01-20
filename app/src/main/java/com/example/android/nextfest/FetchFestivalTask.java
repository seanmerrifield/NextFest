package com.example.android.nextfest;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.nextfest.data.Event;
import com.example.android.nextfest.data.Location;
import com.example.android.nextfest.data.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FetchFestivalTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchFestivalTask.class.getSimpleName();

    private final Context mContext;

    //Initialize instance variables in constructor
    public FetchFestivalTask(Context context){
        mContext = context;
    }

    String[] parseLocationString(String locationString){
        String delims = "[ ,]";
        return locationString.split(delims);
    }

    public JSONArray getEventsJsonArray(String festivalJsonStr){
        final String RESULTS_PAGE_KEY = "resultsPage";
        final String RESULTS_KEY = "results";
        final String EVENT_KEY = "event";
        try {
            JSONObject eventsJSON = new JSONObject(festivalJsonStr);
            eventsJSON = eventsJSON.getJSONObject(RESULTS_PAGE_KEY).getJSONObject(RESULTS_KEY);

            //Gets array of events at location
            return eventsJSON.getJSONArray(EVENT_KEY);
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }
    public Event createEvent(Realm realm, JSONObject eventJson, JSONArray performersArray){
        String EVENT_TYPE_KEY = "type";
        String NAME_KEY = "displayName";
        String START_TIME_KEY = "start";
        String DATE_KEY = "date";
        String TIME_KEY = "time";
        String POPULARITY_KEY = "popularity";

        try {
            //EVENT DATA
            String headliner;
            //If there are performers, find headliner
            if (performersArray.isNull(0)) {
                headliner = "TBD";
            } else {
                //Assumes first object in array is the headliner
                headliner = performersArray.getJSONObject(0).getString(NAME_KEY);
            }

            String eventName = eventJson.getString(NAME_KEY);
            String eventType = eventJson.getString(EVENT_TYPE_KEY);
            double popularity = eventJson.getDouble(POPULARITY_KEY);
            JSONObject dateJson = eventJson.getJSONObject(START_TIME_KEY);
            String dateStr = dateJson.getString(DATE_KEY);
            String timeStr = dateJson.getString(TIME_KEY);


            RealmResults<Event> eventResult = realm.where(Event.class).equalTo("eventName", eventName).findAll();
            Event event;
            if (eventResult.size() != 0){
                event = eventResult.first();
            }
            else{
                event = new Event();
            }

            event.setEventName(eventJson.getString(NAME_KEY));
            event.setHeadliner(headliner);
            event.setDate(Utility.formatDatetoLong(dateStr, "yyyy-MM-dd"));
            event.setTime(Utility.formatTimetoInt(timeStr, "HH:mm:ss"));

            return event;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }
    public Venue createVenue(Realm realm, JSONObject venueJson, Event event){
        String NAME_KEY = "displayName";
        try {
            //VENUE DATA
            String venueString = venueJson.getString(NAME_KEY);

            RealmResults<Venue> venueResult = realm.where(Venue.class).equalTo("venueName", venueString).findAll();
            Venue venue;
            if (venueResult.size() != 0) {
                venue = venueResult.first();
            } else {
                venue = new Venue();

            }
            if (venue.getEvents() == null) {
                venue.setEvents(new RealmList<Event>());
            }
            venue.setVenueName(venueString);
            venue.getEvents().add(event);
            return venue;
            }
        catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }
    public Location createLocation(Realm realm, JSONObject locationJson, String locationSetting, Venue venue){
        final String LATITUDE_KEY = "lat";
        final String LONGITUDE_KEY = "lng";
        final String CITY_KEY = "city";
        try {
            //LOCATION DATA
            String locationStr = locationJson.getString(CITY_KEY);
            String[] locationData = parseLocationString(locationStr);
            String city = locationData[0];
            String country = locationData[1];
            double latitude = locationJson.getDouble(LATITUDE_KEY);
            double longitude = locationJson.getDouble(LONGITUDE_KEY);

            RealmResults<Location> locationResult = realm.where(Location.class).equalTo("locationSetting", Long.parseLong(locationSetting, 10)).findAll();
            Location location;
            if (locationResult.size() != 0) {
                location = locationResult.first();
            } else {
                location = new Location();

            }
            location.setLocationSetting(Long.parseLong(locationSetting, 10));
            location.setCity(city);
            location.setCountry(country);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            if (location.getVenues() == null) {
                location.setVenues(new RealmList<Venue>());
            }
            location.getVenues().add(venue);
            return location;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }


    public String[] getFestivalDataFromJson(String festivalJsonStr, String locationSetting){

        final String VENUE_KEY = "venue";
        final String LOCATION_KEY = "location";
        final String PERFORMANCE_KEY = "performance";

        JSONArray eventsArray = getEventsJsonArray(festivalJsonStr);
        if (eventsArray == null){
            return null;
        }

        String[] resultStrs = new String[eventsArray.length()];
        //All event data will be placed here
        Vector<ContentValues> cVVector = new Vector<ContentValues>(eventsArray.length());


       // RealmConfiguration config = new RealmConfig
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        try {
            for (int i = 0; i < eventsArray.length(); i++) {

                JSONObject eventJson = eventsArray.getJSONObject(i);
                JSONObject venueJson = eventJson.getJSONObject(VENUE_KEY);
                JSONObject locationJson = eventJson.getJSONObject(LOCATION_KEY);
                JSONArray performersArray = eventJson.getJSONArray(PERFORMANCE_KEY);

                Event event = createEvent(realm, eventJson, performersArray);
                if (event == null){
                    return null;
                }
                realm.copyToRealmOrUpdate(event);


                Venue venue = createVenue(realm, venueJson, event);
                if (venue == null){
                    return null;
                }
                realm.copyToRealmOrUpdate(venue);

                Location location = createLocation(realm, locationJson, locationSetting, venue);
                if (location == null){
                    return null;
                }
                realm.copyToRealmOrUpdate(location);



                /*
                long locationId = addLocation(locationSetting, city, country, latitude, longitude);
                long venueId = addVenue(venue, locationId);


                ContentValues eventValues = new ContentValues();


                eventValues.put(FestivalContract.EventEntry.COLUMN_VENUE_KEY, venueId);
                eventValues.put(FestivalContract.EventEntry.COLUMN_EVENT_NAME, eventName);
                eventValues.put(FestivalContract.EventEntry.COLUMN_DATE, dateLong);
                eventValues.put(FestivalContract.EventEntry.COLUMN_TIME, timeInt);
                eventValues.put(FestivalContract.EventEntry.COLUMN_HEADLINER, headliner);

                cVVector.add(eventValues);
                */



                resultStrs[i] = Utility.formatDatetoString(event.getDate()) + " - " +
                                event.getHeadliner() + " - " +
                                venue.getVenueName() + " - " +
                                location.getCity();

                Log.d(LOG_TAG, resultStrs[i]);
            }

            realm.commitTransaction();
            realm.close();
            /*
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

            */
            return resultStrs;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    };

   @Override
    protected Void doInBackground(String... params) {

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
           getFestivalDataFromJson(festivalJsonStr, locationQuery);

       }
       catch (IOException e){
           Log.e(LOG_TAG, "Error ", e);
       }
       /*
        catch (JSONException e){
            Log.e(LOG_TAG,  e.getMessage(), e);
            e.printStackTrace();
        }
        */
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
        return null;
   }

/*
    @Override
    protected void onPostExecute(String[] result) {
        if (result != null){
            mFestivalAdapter.clear();
            for (String eventStr : result){
                mFestivalAdapter.add(eventStr);
            }
        }
    }
*/
}
