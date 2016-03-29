package com.example.android.nextfest;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.nextfest.data.Artist;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FetchArtistTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
    private final Context mContext;

    //Initialize instance variables in constructor
    public FetchArtistTask(Context context){
        mContext = context;
    }

    public Artist createOrUpdateArtist(Realm realm, JSONObject artistJson){
        final String ID_KEY = "id";
        final String NAME_KEY = "name";
        final String IMAGE_KEY = "images";
        final String URL_KEY = "url";
        try {

            String spotifyId = artistJson.getString(ID_KEY);
            String artistName = artistJson.getString(NAME_KEY);
            String imageUrl = artistJson.getJSONArray(IMAGE_KEY).getJSONObject(0).getString(URL_KEY);

            Artist artist = realm.where(Artist.class).equalTo("artistName", artistName).findFirst();
            //If artist doesn't exist, create new Artist and increment ID
            if (artist == null){
                artist = new Artist();
                int nextId;
                //If Artist model is empty set first id = 1
                if (realm.where(Artist.class).max("id") == null){
                    nextId = 1;
                }
                //If Artist object exists then increment id
                else{
                    nextId = realm.where(Artist.class).max("id").intValue() + 1;
                }
                artist.setId(nextId);
            }

            artist.setArtistName(artistName);
            artist.setSpotifyId(spotifyId);
            artist.setImageUrl(imageUrl);

            return artist;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }

    public String getArtistDataFromJson(String artistJsonStr){
        Log.d(LOG_TAG, "Starting getArtistDataFromJSon");
        final String ARTIST_KEY = "artists";
        final String ITEM_KEY = "items";

        String resultStr;

        RealmConfiguration config = new RealmConfiguration.Builder(mContext).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Artist artist;
        try {
            //Create artist instance from JSON
            JSONObject artistJson = new JSONObject(artistJsonStr).getJSONObject(ARTIST_KEY).getJSONArray(ITEM_KEY).getJSONObject(0);
            artist = createOrUpdateArtist(realm, artistJson);


            if (artist != null) {
                //resultStr = artist.getArtistName() + " - " + artist.getSpotifyId();
                realm.copyToRealmOrUpdate(artist);
            }
            else{
                Log.v(LOG_TAG, "createorUpdateArtistFailed");
                resultStr = null;
            }
            realm.commitTransaction();

            //Set result string before closing realm
            resultStr = artist.getArtistName() + " - " + artist.getSpotifyId();
            realm.close();
            return resultStr;
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }

    protected String doInBackground(String... params){
        Log.d(LOG_TAG, "Starting doInBackground");

        if (params.length == 0){
            return null;
        }
        //Assumes first parameter that's passed into FetchArtistTask is the artist name
        String artistName = params[0];
        String artist;

        try {
            artist = URLEncoder.encode(artistName, "utf-8");
        }
        catch(UnsupportedEncodingException e){
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON String that will be obtained from get request
        String artistJsonStr = null;
        String resultStr = null;
        try {
            final String SPOTIFY_BASE_URL = "https://api.spotify.com";
            final String VERSION_PATH = "v1";
            final String SEARCH_PATH = "search";
            final String TYPE_PARAM = "type";
            final String ARTIST_PATH = "artist";
            final String QUERY_PARAM = "q";

            //Build up Uri to access API
            Uri builtUri = Uri.parse(SPOTIFY_BASE_URL).buildUpon()
                    .appendPath(VERSION_PATH)
                    .appendPath(SEARCH_PATH)
                    .appendQueryParameter(QUERY_PARAM, artist)
                    .appendQueryParameter(TYPE_PARAM, ARTIST_PATH)
                    .build();

            Log.d(LOG_TAG, "Fetch URL - " + builtUri.toString());

            URL url = new URL(builtUri.toString());

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

            artistJsonStr = buffer.toString();
            resultStr = getArtistDataFromJson(artistJsonStr);
            Log.d(LOG_TAG, "Artist ID is: " + resultStr);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
        }
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
    protected void onPostExecute(String result){
        if (result != null){
            Log.v(LOG_TAG, "Result is: " + result);
        }
        else{
            Log.e(LOG_TAG, "Artist data not found");
        }
    }
*/
}
