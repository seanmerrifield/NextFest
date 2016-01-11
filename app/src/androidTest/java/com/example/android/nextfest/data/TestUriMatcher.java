package com.example.android.nextfest.data;


import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String VENUE_QUERY = "Ministry of Sound";
    private static final String LOCATION_QUERY = "London";

    private static final Uri TEST_EVENT_DIR = FestivalContract.EventEntry.CONTENT_URI;
    private static final Uri TEST_EVENT_WITH_VENUE_DIR = FestivalContract.EventEntry.buildEventVenue(VENUE_QUERY);

    private static final Uri TEST_VENUE_DIR = FestivalContract.VenueEntry.CONTENT_URI;
    private static final Uri TEST_VENUE_WITH_LOCATION_DIR = FestivalContract.VenueEntry.buildVenueLocation(LOCATION_QUERY);

    private static final Uri TEST_LOCATION_DIR = FestivalContract.LocationEntry.CONTENT_URI;

    public void testUriMatcher(){
        UriMatcher testMatcher = FestivalProvider.buildUriMatcher();

        assertEquals("Error: The EVENT URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_DIR), FestivalProvider.EVENTS);
        assertEquals("Error: The EVENT WITH VENUE URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_WITH_VENUE_DIR), FestivalProvider.EVENT_WITH_VENUE);

        assertEquals("Error: The VENUE URI was matched incorrectly.",
                testMatcher.match(TEST_VENUE_DIR), FestivalProvider.VENUES);
        assertEquals("Error: The VENUE WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_VENUE_WITH_LOCATION_DIR), FestivalProvider.VENUE_WITH_LOCATION);

        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_DIR), FestivalProvider.LOCATIONS);
    }


}
