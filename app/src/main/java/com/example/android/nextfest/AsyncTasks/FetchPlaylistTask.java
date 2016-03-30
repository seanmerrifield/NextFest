package com.example.android.nextfest.AsyncTasks;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.nextfest.data.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FetchPlaylistTask extends AsyncTask<String, Void, String>{
    private final String LOG_TAG = FetchPlaylistTask.class.getSimpleName();
    private final Context mContext;

    //Initialize instance variables in constructor
    public FetchPlaylistTask(Context context){
        mContext = context;
    }

    private Track createOrUpdateTrack(Realm realm, HashMap trackHash){
        final String colTrackName = "trackName";
        final String colSpotifyId = "spotifyId";
        final String colArtistName = "artistName";

        String trackName = (String) trackHash.get(colTrackName);
        String spotifyId = (String) trackHash.get(colSpotifyId);
        String artistName = (String) trackHash.get(colArtistName);

        Track track = realm.where(Track.class).equalTo(colTrackName, trackName).findFirst();

        //If track doesn't exist, create new Track and increment ID
        if (track == null){
            track = new Track();
            int nextId;
            //If Artist model is empty set first id = 1
            if (realm.where(Track.class).max("id") == null){
                nextId = 1;
            }
            //If Artist object exists then increment id
            else{
                nextId = realm.where(Track.class).max("id").intValue() + 1;
            }
            track.setId(nextId);
        }

        track.setTrackName(trackName);
        track.setArtistName(artistName);
        track.setSpotifyId(spotifyId);

        return track;
    }

    private String[] getPlaylistDataFromJson(String playlistJsonStr, String artistName){
        String TRACK_KEY = "tracks";
        String NAME_KEY = "name";
        String ID_KEY = "id";

        //Create hash map to store all data obtained from JSON
        HashMap <String, String> trackHash = new HashMap<String, String>();
        final String colTrackName = "trackName";
        final String colSpotifyId = "spotifyId";
        final String colArtistName = "artistName";

        try {
            JSONObject playlistJson = new JSONObject(playlistJsonStr);
            JSONArray tracksArray = playlistJson.getJSONArray(TRACK_KEY);

            String[] resultStrs = new String[tracksArray.length()];
            RealmConfiguration config = new RealmConfiguration.Builder(mContext).deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);

            // RealmConfiguration config = new RealmConfig
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();


            //Add each track from JSON to Track model
            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackJson = tracksArray.getJSONObject(i);

                trackHash.put(colTrackName, trackJson.getString(NAME_KEY));
                trackHash.put(colSpotifyId, trackJson.getString(ID_KEY));
                trackHash.put(colArtistName, artistName);

                Track track = createOrUpdateTrack(realm, trackHash);
                realm.copyToRealmOrUpdate(track);

                resultStrs[i] = track.getArtistName() + " - " +
                                track.getTrackName() + " - " +
                                track.getSpotifyId();

                Log.d(LOG_TAG, resultStrs[i]);

            }

            realm.commitTransaction();
            return resultStrs;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }

    protected String doInBackground(String... params) {
        Log.d(LOG_TAG, "Starting doInBackground");

        if (params.length == 0) {
            return null;
        }
        //Assumes first parameter that's passed into FetchArtistTask is the artist name
        String artistSpotifyId = params[0];
        String artistName = params[1];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON String that will be obtained from get request
        String playlistJsonStr = null;
        String resultStr = null;


        try {
            final String SPOTIFY_BASE_URL = "https://api.spotify.com";
            final String VERSION_PATH = "v1";
            final String ARTISTS_PATH = "artists";
            final String TOP_TRACKS_PATH = "top-tracks";
            final String COUNTRY_PATH = "country";
            final String NETHERLANDS_PATH = "NL";


            //Build up Uri to access API
            Uri builtUri = Uri.parse(SPOTIFY_BASE_URL).buildUpon()
                    .appendPath(VERSION_PATH)
                    .appendPath(ARTISTS_PATH)
                    .appendPath(artistSpotifyId)
                    .appendPath(TOP_TRACKS_PATH)
                    .appendQueryParameter(COUNTRY_PATH, NETHERLANDS_PATH)
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

            //Obtain JSON string and parse
            playlistJsonStr = buffer.toString();
            getPlaylistDataFromJson(playlistJsonStr, artistName);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
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
}
