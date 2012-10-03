package com.bluebottlesoftware.nationalparkclosures.parsers;

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
    public static String getDisplayFriendlyDateForRegion(int region, String date)
    {
        String friendlyDate;
        switch(region)
        {
        case Region.Nsw:
            friendlyDate = getFriendlyDateForNsw(date);
            break;
            
        default:
            throw new IllegalArgumentException("Region " + region + " not supported");
        }
        return friendlyDate;
    }

    /**
     * Returns a date that's appropriately formatted for display based on the NSW format date provided
     * @param date
     * @return
     */
    private static String getFriendlyDateForNsw(String date)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
