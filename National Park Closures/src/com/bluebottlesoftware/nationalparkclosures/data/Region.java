package com.bluebottlesoftware.nationalparkclosures.data;

import com.bluebottlesoftware.parkclosures.R;

/**
 * Enumeration of states and regions that are enumeratable. Currently this is
 * just states but will be extended to other regions. For example QLD has a
 * number of feeds that are specific to regions while NSW has just feeds for the
 * state as a whole
 */
public class Region
{
    // These are the IDs that are stored in the profile database
    public static final int Nsw = 0;
    public static final int Qld = 1;
    public static final int NswFireIncidents = 2;
    public static final int VicFireIncidents = 3;
    public static final int QldFireIncidents = 4;
    public static final int TasFireEntireState = 5;
    public static final int SaFireEntireState = 6;
    public static final int WaFireIncidents = 7;
    /**
     * Feed addresses for the various states and regions
     */
    private static final String NswNpUrl = "http://www.environment.nsw.gov.au/nationalparks/rss/fireclosure.aspx";
    private static final String QldNpUrl = "http://www.nprsr.qld.gov.au/xml/rss/parkalerts.xml";

    private static final String NswFireUrl = "http://www.rfs.nsw.gov.au/feeds/majorIncidents.xml";
    private static final String VicFireUrl = "http://osom.cfa.vic.gov.au/public/osom/IN_COMING.rss";
    private static final String QldFireUrl = "http://www.ruralfire.qld.gov.au/bushfirealert/bushfireAlert.xml";
    private static final String TasFireUrl = "http://www.fire.tas.gov.au/Show?pageId=colBushfireSummariesRss";
    private static final String SaFireUrl = "http://www.cfs.sa.gov.au/custom/criimson/CFS_Current_Incidents.xml";
    private static final String WaFireUrl = "http://www.fesa.wa.gov.au/alerts/_layouts/fesa.sps2010.internet/fesalistfeed.aspx?List=e2064c8d-e111-41e1-8925-d249098d1a5e&View=b26d1f1b-0afc-4a65-8592-1c31c3af1323";

    private static final String NswBaseUrl = "http://environment.nsw.gov.au";
    private static final String WaBaseUrl = "http://www.fesa.wa.gov.au";

    public static String getFeedForRegion(int stateOrRegion)
            throws IllegalArgumentException
    {
        String url;
        switch (stateOrRegion)
        {
        case Nsw:
            url = NswNpUrl;
            break;

        case Qld:
            url = QldNpUrl;
            break;

        case WaFireIncidents:
            url = WaFireUrl;
            break;

        case TasFireEntireState:
            url = TasFireUrl;
            break;

        case SaFireEntireState:
            url = SaFireUrl;
            break;

        case NswFireIncidents:
            url = NswFireUrl;
            break;

        case VicFireIncidents:
            url = VicFireUrl;
            break;

        case QldFireIncidents:
            url = QldFireUrl;
            break;

        default:
            throw new IllegalArgumentException("State or region "
                    + stateOrRegion + " not supported");
        }
        return url;
    }

    /**
     * Returns the current region as a string identifier into the resources
     * directory
     * 
     * @param mRegion
     * @return
     */
    public static int getAsStringId(int region)
    {
        int id;
        switch (region)
        {
        case Nsw:
        default:
            id = R.string.nsw;
            break;

        case Qld:
            id = R.string.qld;
            break;

        case WaFireIncidents:
            id = R.string.waStateFire;
            break;

        case TasFireEntireState:
            id = R.string.tasStateFire;
            break;

        case SaFireEntireState:
            id = R.string.saStateFire;
            break;

        case NswFireIncidents:
            id = R.string.nswStateFire;
            break;
            
        case VicFireIncidents:
            id = R.string.vicStateFire;
            break;
            
        case QldFireIncidents:
            id = R.string.qldStateFire;
            break;
        }
        return id;
    }

    /**
     * Returns the base url for the region
     * 
     * @param region
     * @return
     */
    public static String getBaseUrlForRegion(int region)
    {
        String baseUrl = null;
        switch (region)
        {
        case Nsw:
            baseUrl = NswBaseUrl;
            break;

        case WaFireIncidents:
            baseUrl = WaBaseUrl;
            break;
        }
        return baseUrl;
    }

    // Returns the region corresponding to the region array string
    public static int itemPositionToRegion(int itemPosition)
    {
        int region = Nsw;
        switch(itemPosition)
        {
        case 1:
            region = Qld;
            break;
            
        case 2:
            region = NswFireIncidents;
            break;
            
        case 3:
            region = VicFireIncidents;
            break;
            
        case 4:
            region = QldFireIncidents;
            break;
            
        case 5:
            region = TasFireEntireState;
            break;
            
        case 6:
            region = SaFireEntireState;
            break;
            
        case 7:
            region = WaFireIncidents;
            break;
        }
        return region;
    }
}
