package com.example.android.nextfest;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {

    static final String LOG_TAG = Utility.class.getSimpleName();

    static long formatDatetoLong(String dateString, String format){
        long dateLong;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(dateString);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            dateLong = calendar.getTimeInMillis();

        }
        catch(ParseException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "Date string parse failed");
            dateLong = 0;
        }

        return dateLong;
    }

 static String formatDatetoString(long dateInMillis){
     Date date = new Date(dateInMillis);
     Calendar calendar = new GregorianCalendar();
     return DateFormat.getDateInstance().format(date);
 }

}
