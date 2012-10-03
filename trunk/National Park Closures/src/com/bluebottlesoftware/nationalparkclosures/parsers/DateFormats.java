package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.data.Region;

/**
 * Class that defines the various date formats that are used in the feeds for the individual states
 * @author lee
 *
 */
public class DateFormats
{   
    public static final String NswDateFormat = "E, dd MMM yyyy HH:mm:ss";   /**<Date format used by NSW feed*/
    
    public static String getDateFormatForState(int state)
    {
        String dateFormat;
        switch(state)
        {
        case Region.Nsw:
            dateFormat = NswDateFormat;
            break;
            
        default:
            throw new IllegalArgumentException("Invalid state " + state + " requested");
        }
        return dateFormat;
    }
    
    /**
     * Returns the date formatted appropriately for display in the list view
     * @param date
     * @return
     */
    public static String convertDate(String srcDate,String srcDateFormat,String destDateFormat)
    {
        DateFormat srcFormat = new SimpleDateFormat(srcDateFormat);

        String friendlyDate = srcDate;
        try
        {
            Date src = srcFormat.parse(srcDate);
            DateFormat dstFormat = new SimpleDateFormat(destDateFormat);
            friendlyDate = dstFormat.format(src); 
        }
        catch(ParseException e)
        {
            Log.e("getDisplayFriendlyDateForRegion","Caught ParseException on date");
        }
        return friendlyDate;
    }
}
