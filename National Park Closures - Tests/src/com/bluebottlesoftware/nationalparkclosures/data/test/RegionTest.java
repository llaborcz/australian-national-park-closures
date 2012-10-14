package com.bluebottlesoftware.nationalparkclosures.data.test;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nswnpclosures.R;

import junit.framework.TestCase;

public class RegionTest extends TestCase
{
    public void testRegionAsId()
    {
        int [] regions = new int [] {Region.Nsw,Region.Nt,Region.Qld,Region.Sa,Region.Tas,Region.Vic,Region.Wa};
        int [] stringIds = new int [] {R.string.nsw,R.string.nt,R.string.qld,R.string.sa,R.string.tas,R.string.vic,R.string.wa};
        
        for(int i = 0;i<regions.length;i++)
        {
            int stringId = Region.getAsStringId(regions[i]);
            assertEquals(stringIds[i],stringId);
        }
    }
    
}
