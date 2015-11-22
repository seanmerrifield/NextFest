package com.example.android.nextfest;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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

    public String[] getFestivalDataFromJson(String festivalJsonStr){

        final String POPULARITY_KEY = "popularity";
        final String VENUE_KEY = "venue";
        final String LOCATION_KEY = "location";
        final String CITY_KEY = "city";
        final String EVENT_TYPE_KEY = "type";
        final String NAME_KEY = "displayName";
        final String START_TIME_KEY = "start";
        final String DATE_KEY = "date";
        final String TIME_KEY = "time";


        final String RESULTS_PAGE_KEY = "resultsPage";
        final String RESULTS_KEY = "results";
        final String EVENT_KEY = "event";


        try {
            JSONObject eventsJSON = new JSONObject(festivalJsonStr);
            eventsJSON = eventsJSON.getJSONObject(RESULTS_PAGE_KEY).getJSONObject(RESULTS_KEY);
            //Gets array of events at location
            JSONArray eventsArray = eventsJSON.getJSONArray(EVENT_KEY);

            String[] resultStrs = new String[eventsArray.length()];
            for (int i = 0; i < eventsArray.length(); i++){

                JSONObject eventJson = eventsArray.getJSONObject(i);
                JSONObject venueJson = eventJson.getJSONObject(VENUE_KEY);
                JSONObject locationJson = eventJson.getJSONObject(LOCATION_KEY);
                JSONObject dateJson = eventJson.getJSONObject(START_TIME_KEY);

                String eventType = eventJson.getString(EVENT_TYPE_KEY);
                String eventName = eventJson.getString(NAME_KEY);
                double popularity = eventJson.getDouble(POPULARITY_KEY);
                String venue = venueJson.getString(NAME_KEY);
                String location = locationJson.getString(CITY_KEY);
                String dateStr = dateJson.getString(DATE_KEY);

               Calendar cal = formatDateString(dateStr, "yyyy-MM-dd");

                resultStrs[i] = eventName + " - " + dateStr;

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }

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

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON String that will be obtained from get request
        String festivalJsonStr = null;

       //query parameters
        String format = "json";

       try {
           final String FESTIVAL_BASE_URL = "http://api.songkick.com/api/3.0/metro_areas/31366/calendar.json?apikey=7xVXPkI8sUxkF8wb";

           URL url = new URL(FESTIVAL_BASE_URL);

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


       //This will only happen if there was an error retrieving the data;
       //return null;
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
