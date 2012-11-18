package com.bluebottlesoftware.nationalparkclosures.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.Log;

/**
 * Class that defines the various date formats that are used in the feeds for the individual states
 * @author lee
 *
 */
public class CalendarUtils
{   
    public static final String DateFormat = "E, dd MMM yyyy HH:mm:ss";       /**<Date format used by NSW feed*/
    
    public static String getDateFormatForRegion(int state)
    {
        return DateFormat;
    }
    
    /**
     * Returns the date formatted appropriately for display in the list view
     * @param date
     * @return
     */
    public static String convertDate(String srcDate,String srcDateFormat,String destDateFormat)
    {
        DateFormat srcFormat = new SimpleDateFormat(destDateFormat,Locale.US);

        String friendlyDate = srcDate;
        try
        {
            Date src = srcFormat.parse(srcDate);
            DateFormat dstFormat = new SimpleDateFormat(destDateFormat,Locale.US);
            friendlyDate = dstFormat.format(src); 
        }
        catch(ParseException e)
        {
            Log.e("convertDate","Caught ParseException on date "+e.getMessage());
        }
        return friendlyDate;
    }
    
    
    /**
     * Returns a calendar object based on the date provided and the format of that date
     * @param date
     * @param dateFormat
     * @return
     * @throws ParseException 
     */
    public static Calendar createCalendarFromDateAndFormat(String itemDate,String itemDateFormat)
    {
        Calendar cal  = new GregorianCalendar();
        try
        {
            DateFormat dateFormat = new SimpleDateFormat(itemDateFormat,Locale.US);
            Date date = dateFormat.parse(itemDate);
            cal.setTime(date);
        }   
        catch(ParseException e)
        {
            Log.e("createCalendarFromDateAndFormat","Caught ParseException while processing date");
        }
        return cal;
    }

}
