package com.bluebottlesoftware.nationalparkclosures.data.test;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.parkclosures.R;

import junit.framework.TestCase;

public class RegionTest extends TestCase
{
    public void testRegionAsId()
    {
        int [] regions = new int [] {Region.Nsw,Region.Qld,Region.WaFireIncidents};
        int [] stringIds = new int [] {R.string.nsw,R.string.qld,R.string.waStateFire};
        
        for(int i = 0;i<regions.length;i++)
        {
            int stringId = Region.getAsStringId(regions[i]);
            assertEquals(stringIds[i],stringId);
        }
    }
    
}
