package com.bluebottlesoftware.nationalparkclosures.parsers.test;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.parsers.DateFormats;

import junit.framework.TestCase;

public class DateFormatsTest extends TestCase
{
    /**
     * Tests that the date that is converted from the NSW data feed date format 
     * is converted to the appropriate format
     */
    public void testValidNswDate()
    {
        String [] inputDataSet  = {"Mon, 10 Sep 2012 11:50:00","Wed, 12 Sep 2012 12:17:00","Mon, 24 Sep 2012 05:53:00"};
        String [] outputDataSet = {"Mon, 10 Sep 2012","Wed, 12 Sep 2012","Mon, 24 Sep 2012"};
        
        int i = 0;
        for(String input : inputDataSet)
        {
            String output = DateFormats.getDisplayFriendlyDateForRegion(Region.Nsw, input);
            assertEquals(outputDataSet[i], output);
            i++;
        }
    }
}
