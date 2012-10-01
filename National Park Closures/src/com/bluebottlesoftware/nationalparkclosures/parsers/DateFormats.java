package com.bluebottlesoftware.nationalparkclosures.parsers;

import com.bluebottlesoftware.nationalparkclosures.data.State;

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
        case State.Nsw:
            dateFormat = NswDateFormat;
            break;
            
        default:
            throw new IllegalArgumentException("Invalid state " + state + " requested");
        }
        return dateFormat;
    }
}
