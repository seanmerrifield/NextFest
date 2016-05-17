package com.example.android.nextfest;


import android.util.Log;

import com.example.android.nextfest.data.Event;
import com.example.android.nextfest.data.Location;
import com.example.android.nextfest.data.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class SongKickService {


    public SongKickService(){}


    public static Event createEvent(Realm realm, JSONObject eventJson, Location location, Venue venue){
        final String ID_KEY = "id";
        final String EVENT_TYPE_KEY = "type";
        final String NAME_KEY = "displayName";
        final String START_TIME_KEY = "start";
        final String DATE_KEY = "date";
        final String TIME_KEY = "time";

        final String PERFORMANCE_KEY = "performance";


        try {

            JSONArray performersArray = eventJson.getJSONArray(PERFORMANCE_KEY);

            //EVENT DATA
            String headliner;
            //If there are performers, find headliner
            if (performersArray.isNull(0)) {
                headliner = "TBD";
            } else {
                //Assumes first object in array is the headliner
                headliner = performersArray.getJSONObject(0).getString(NAME_KEY);
            }

            int songKickId = eventJson.getInt(ID_KEY);
            String eventName = eventJson.getString(NAME_KEY);
            String eventType = eventJson.getString(EVENT_TYPE_KEY);
            JSONObject dateJson = eventJson.getJSONObject(START_TIME_KEY);

            Event event = new Event();
            event.setEventName(eventName);
            event.setHeadliner(headliner);
            event.setType(eventType);
            event.setSongkickId(songKickId);
            event.setDate(Utility.formatDatetoLong(dateJson.getString(DATE_KEY), "yyyy-MM-dd"));
            event.setTime(Utility.formatTimetoInt(dateJson.getString(TIME_KEY), "HH:mm:ss"));
            event.setVenue(venue);
            event.setLocation(location);
            return event;
        }
        catch(JSONException e){
            Log.e(SongKickService.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }
    public static Venue createVenue(Realm realm, JSONObject eventJson, Location location){
        final String VENUE_KEY = "venue";
        final String NAME_KEY = "displayName";
        final String LATITUDE_KEY = "lat";
        final String LONGITUDE_KEY = "lng";


        try {
            JSONObject venueJson = eventJson.getJSONObject(VENUE_KEY);

            //VENUE DATA from JSON
            String venueString = venueJson.getString(NAME_KEY);
            double latitude = venueJson.getDouble(LATITUDE_KEY);
            double longitude = venueJson.getDouble(LONGITUDE_KEY);

            Venue venue = new Venue();

            venue.setVenueName(venueString);
            venue.setLatitude(latitude);
            venue.setLongitude(longitude);
            venue.setLocation(location);
            return venue;
        }
        catch(JSONException e) {
            Log.e(SongKickService.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }
    public static  Location createLocation(Realm realm, JSONObject eventJson, String locationSetting){

        final String LOCATION_KEY = "location";
        final String LATITUDE_KEY = "lat";
        final String LONGITUDE_KEY = "lng";
        final String CITY_KEY = "city";

        final String PRIMARY_KEY = "locationSetting";
        try {

            JSONObject locationJson = eventJson.getJSONObject(LOCATION_KEY);

            //LOCATION DATA
            String locationStr = locationJson.getString(CITY_KEY);
            String[] locationData = parseLocationString(locationStr);
            String city = locationData[0];
            String country = locationData[1];

            double latitude = locationJson.getDouble(LATITUDE_KEY);
            double longitude = locationJson.getDouble(LONGITUDE_KEY);

            Location location = new Location();

            location.setLocationSetting(Long.parseLong(locationSetting, 10));
            location.setCity(city);
            location.setCountry(country);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            return location;
        }
        catch(JSONException e){
            Log.e(SongKickService.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    public static String[] parseLocationString(String locationString){
        String delims = "[ ,]";
        return locationString.split(delims);
    }
}

