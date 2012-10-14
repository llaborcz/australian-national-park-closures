package com.bluebottlesoftware.nationalparkclosures.data;

import com.bluebottlesoftware.nswnpclosures.R;

/**
 * Enumeration of states and regions that are enumeratable.
 * Currently this is just states but will be extended to other regions. For example QLD has a number
 * of feeds that are specific to regions while NSW has just feeds for the state as a whole
 */
public class Region
{
    public static final int Nsw = 0;
    public static final int Qld = 1;
    public static final int Nt  = 3;
    public static final int Wa  = 4;
    public static final int Sa  = 5;
    public static final int Vic = 6;
    public static final int Tas = 7;

    /**
     * Feed addresses for the various states and regions
     */
    private static final String NswFeedAddress = "http://data.nsw.gov.au/redirect.php?title=National+Park+Fire%2C+Flood+and+Park+Closure+Updates.&file=XML";
    private static final String QldFeedAddress = "http://www.nprsr.qld.gov.au/xml/rss/parkalerts.xml";
    
    public static String getFeedForStateOrRegion(int stateOrRegion) throws IllegalArgumentException
    {
        String url;
        switch(stateOrRegion)
        {
        case Nsw:
            url = NswFeedAddress;
            break;
            
        case Qld:
            url = QldFeedAddress;
            break;
            
        default:
            throw new IllegalArgumentException("State or region " + stateOrRegion + " not supported");
        }
        return url;
    }

    /**
     * Returns the current region as a string identifier into the resources directory
     * @param mRegion
     * @return
     */
    public static int getAsStringId(int region)
    {
        int id;
        switch(region)
        {
        case Nsw:
        default:
            id = R.string.nsw;
            break;
            
        case Qld:
            id = R.string.qld;
            break;
            
        case Nt:
            id = R.string.nt;
            break;
            
        case Wa:
            id = R.string.wa;
            break;
            
        case Sa:
            id = R.string.sa;
            break;
            
        case Vic:
            id = R.string.vic;
            break;
            
        case Tas:
            id = R.string.tas;
            break;
        }
        return id;
    }
}
