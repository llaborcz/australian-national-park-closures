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
    private static final String NswFeedAddress = "http://www.environment.nsw.gov.au/nationalparks/rss/fireclosure.aspx";
    private static final String QldFeedAddress = "http://www.nprsr.qld.gov.au/xml/rss/parkalerts.xml";
    private static final String WaFeedAddress  = "http://www.fesa.wa.gov.au/alerts/_layouts/fesa.sps2010.internet/fesalistfeed.aspx?List=e2064c8d-e111-41e1-8925-d249098d1a5e&View=b26d1f1b-0afc-4a65-8592-1c31c3af1323";
    
    private static final String NswBaseUrl = "http://environment.nsw.gov.au";
    private static final String WaBaseUrl  = "http://www.fesa.wa.gov.au";
    
    public static String getFeedForRegion(int stateOrRegion) throws IllegalArgumentException
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
            
        case Wa:
            url = WaFeedAddress;
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

    /**
     * Returns the base url for the region
     * @param region
     * @return
     */
    public static String getBaseUrlForRegion(int region)
    {
        String baseUrl = null;
        switch(region)
        {
        case Nsw:
            baseUrl = NswBaseUrl;
            break;
            
        case Wa:
            baseUrl = WaBaseUrl;
            break;
        }
        return baseUrl;
    }
}