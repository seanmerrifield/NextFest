package com.example.android.nextfest.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;



public class TestProvider extends AndroidTestCase{

    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                FestivalProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: FestivalProvider registered with authorrity: " + providerInfo.authority +
                            " instead of authority: " + FestivalContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FestivalContract.CONTENT_AUTHORITY);
        }

        catch(PackageManager.NameNotFoundException e){
            assertTrue("Error: FestivalProvider not registered at " + mContext.getPackageName(), false);
        }
    }


    public void testGetType(){

        // Test: event Type
        // content://com.example.android.nextfest/event
        String type = mContext.getContentResolver().getType(FestivalContract.EventEntry.CONTENT_URI);
        assertEquals("Error: the EventEntry CONTENT_URI should return EventEntry.CONTENT_URI",
                FestivalContract.EventEntry.CONTENT_TYPE, type);

        // Test: Event with venue Type
        // content://com.example.android.nextfest/event/paradiso
        String testVenue = "12094";
        type = mContext.getContentResolver().getType(FestivalContract.EventEntry.buildEventVenue(testVenue));
        assertEquals("Error: the EventEntry CONTENT_URI with locaton should return EventEntry.CONTENT_URI",
                FestivalContract.EventEntry.CONTENT_TYPE, type);

        // Test: Venue Type
        // content://com.example.android.nextfest/venue
        type = mContext.getContentResolver().getType(FestivalContract.VenueEntry.CONTENT_URI);
        assertEquals("Error: the VenueEntry CONTENT_URI should return VenueEntry.CONTENT_URI",
                FestivalContract.VenueEntry.CONTENT_TYPE, type);




    }
}
