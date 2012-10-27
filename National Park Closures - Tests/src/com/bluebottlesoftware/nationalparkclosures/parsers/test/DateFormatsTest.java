package com.bluebottlesoftware.nationalparkclosures.parsers.test;

import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
import com.bluebottlesoftware.nationalparkclosures.data.FeedDataAdapter;

import junit.framework.TestCase;

public class DateFormatsTest extends TestCase
{
    /**
     * Tests that the date that is converted from the NSW data feed date format 
     * is converted to the appropriate format
     */
    public void testValidNswDates()
    {
        String [] inputDataSet  = {"Mon, 10 Sep 2012 11:50:00","Wed, 12 Sep 2012 12:17:00","Mon, 24 Sep 2012 05:53:00"};
        String [] outputDataSet = {"Mon, 10 Sep 2012","Wed, 12 Sep 2012","Mon, 24 Sep 2012"};
        
        testValidDates(inputDataSet,outputDataSet,CalendarUtils.NswDateFormat,FeedDataAdapter.DisplayDateFormat);
    }
    
    public void testValidQldDates()
    {
        String [] inputDataSet  = {"Fri, 26 Oct 2012 23:00:00 +1000","Thu, 25 Oct 2012 00:00:00 +1000","Thu, 25 Oct 2012 00:00:00 +1000"};
        String [] outputDataSet = {"Fri, 26 Oct 2012","Thu, 25 Oct 2012","Thu, 25 Oct 2012"};
        testValidDates(inputDataSet,outputDataSet,CalendarUtils.QldDateFormat,FeedDataAdapter.DisplayDateFormat);
    }
    
    private void testValidDates(String [] inputDataSet,String [] outputDataSet,String dateFormat,String displayDateFormat)
    {
        int i = 0;
        for(String input : inputDataSet)
        {
            String output = CalendarUtils.convertDate(input, dateFormat,displayDateFormat);
            assertTrue(output.equals(outputDataSet[i]));
            i++;
        }
    }
    
    /**
     * Feeds a bunch of dates that don't correspond to the nsw date but which should just give us back what was passed in
     */
    public void testInvalidNswDates()
    {
        String [] inputDataSet  = {"ert, 10 dp 2012 11:50:00","Wed, 1209 SSep 2012","asdfasfasdfsadf asdf ep 2012 05:53:00"};
        
        for(String input : inputDataSet)
        {
            String output = CalendarUtils.convertDate(input, CalendarUtils.NswDateFormat, FeedDataAdapter.DisplayDateFormat);
            Log.d(input,output);
            assertTrue(output.equals(input));
        }
    }
    
    public void testNswTimes()
    {
        String [] inputDataSet  = {"Mon, 10 Sep 2012 11:50:00","Wed, 12 Sep 2012 12:17:00","Mon, 24 Sep 2012 05:53:00"};
        String [] outputDataSet = {"11:50","12:17","05:53"};
        innerTestTimes(inputDataSet, outputDataSet, CalendarUtils.NswDateFormat, FeedDataAdapter.DisplayTimeFormat);
    }
    
    public void testQldTimes()
    {
        String [] inputDataSet  = {"Fri, 26 Oct 2012 23:00:00 +1000","Thu, 25 Oct 2012 06:34:00 +1000","Thu, 25 Oct 2012 00:00:00 +1000"};
        String [] outputDataSet = {"23:00","06:34","00:00"};
        innerTestTimes(inputDataSet, outputDataSet, CalendarUtils.QldDateFormat, FeedDataAdapter.DisplayTimeFormat);
    }
    
    private void innerTestTimes(String [] inputDataSet, String [] outputDataSet,String intputDateFormat,String displayTimeFormat)
    {
        int i = 0;
        for(String input : inputDataSet)
        {
            String output = CalendarUtils.convertDate(input, intputDateFormat,displayTimeFormat);
            assertTrue(output.equals(outputDataSet[i]));
            i++;
        }
    }
}
