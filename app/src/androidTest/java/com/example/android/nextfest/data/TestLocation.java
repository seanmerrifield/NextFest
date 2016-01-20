package com.example.android.nextfest.data;

import android.test.AndroidTestCase;

import io.realm.Realm;
import io.realm.RealmResults;


public class TestLocation extends AndroidTestCase
{@Override
     protected void setUp() throws Exception {
        super.setUp();
        deleteRecordsFromLocation;
    }


    public void testLocationValues(){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        try {
            Location location = realm.createObject(Location.class);

            location.setLocationSetting(TestUtilities.LOCATION_SETTING);
            location.setCity(TestUtilities.CITY);
            location.setCountry(TestUtilities.COUNTRY);
            location.setLatitude(TestUtilities.LATITUDE);
            location.setLongitude(TestUtilities.LONGITUDE);
            realm.commitTransaction();

            RealmResults<Location> query = realm.where(Location.class).equalTo("city", TestUtilities.CITY).findAll();
        }
        assertTrue("Location query did not return anything", query.isLoaded());
        realm.close()
    }
}
