package com.example.android.nextfest;


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

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

    public String[] updateDbWithJson(String festivalJsonStr, String locationSetting){

        JSONArray eventsArray = getEventsJsonArray(festivalJsonStr);
        if (eventsArray == null){
            Log.e(LOG_TAG, "Events JSON Array is empty");
            return null;
        }

        String[] resultStrs = new String[eventsArray.length()];

        RealmConfiguration config = new RealmConfiguration.Builder(mContext).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        try {

            JSONObject eventJson;

            Event event;
            Venue venue;
            Location location;

            for (int i = 0; i < eventsArray.length(); i++) {

                eventJson = eventsArray.getJSONObject(i);

                location = SongKickService.createLocation(realm, eventJson, locationSetting);
                if(location == null){continue;}

                venue = SongKickService.createVenue(realm, eventJson, location);
                if(venue == null){continue;}

                event = SongKickService.createEvent(realm, eventJson, location, venue);
                if(event == null){continue;}

                realm.copyToRealmOrUpdate(location);
                realm.copyToRealmOrUpdate(venue);
                realm.copyToRealmOrUpdate(event);

                 resultStrs[i] = event.getHeadliner() + " - " +
                                event.getVenue().getVenueName() + " - " +
                                event.getLocation().getCity();

                Log.d(LOG_TAG, resultStrs[i]);


            }

            realm.commitTransaction();
            realm.close();

            return resultStrs;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    };

    public String getFestivalJsonStr(String locationQuery){

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

            Log.d(LOG_TAG, "Fetch URL - " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            Log.d(LOG_TAG, "Creating Buffered Reader");
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
            return festivalJsonStr;
        }
    }

   @Override
    protected Void doInBackground(String... params) {

       Log.d(LOG_TAG, "Starting doInBackground");

       if (params.length == 0){
           return null;
       }

       //Assumes first parameter that's passed into FetchFestivalTask is the location key
       String locationQuery = params[0];

       //Obtains JSON String of Festival Events/Venues/Locations
       String festivalJsonStr = getFestivalJsonStr(locationQuery);
        if (festivalJsonStr == null){
            return null;
        }

       //Update database
       Log.d(LOG_TAG, "Getting Festival Data from JSON...");
       updateDbWithJson(festivalJsonStr, locationQuery);

        return null;
   }

}
