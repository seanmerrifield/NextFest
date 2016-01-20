package com.example.android.nextfest.data;


import android.net.Uri;
import android.test.AndroidTestCase;

public class TestFestivalContract extends AndroidTestCase {
    public void testBuildEventWithVenue(){
        Uri venueUri = FestivalContract.EventEntry.buildEventVenue(TestUtilities.VENUE_NAME);
        assertNotNull("Error: Null Uri returned.", venueUri);

        assertEquals("Error: Venue not properly appended to the end of the Uri",
                TestUtilities.VENUE_NAME, venueUri.getLastPathSegment());

        assertEquals("Error: Event with Venue Uri doesn't match expected result",
                venueUri.toString(), "content://com.example.android.nextfest/event/" + TestUtilities.VENUE_NAME);

    }

    public void testGetVenueFromEvent(){
        Uri venueUri = FestivalContract.EventEntry.buildEventVenue(TestUtilities.VENUE_NAME);

        assertEquals("Error: Venue not properly retrieved", TestUtilities.VENUE_NAME,
                FestivalContract.EventEntry.getVenueFromUri(venueUri));
    }
}
