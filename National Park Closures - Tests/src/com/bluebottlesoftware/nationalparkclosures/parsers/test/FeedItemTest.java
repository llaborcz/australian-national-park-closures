package com.bluebottlesoftware.nationalparkclosures.parsers.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

import junit.framework.TestCase;

public class FeedItemTest extends TestCase
{
    private static final String nswTestDate1  = "Mon, 24 Sep 2012 05:53:00";
    private static final String title = "this is the title";
    private static final String link  = "http://www.google.com";
    private static final String guid  = "234234-34534532-234234";
    private static final String description = "This is a description string";
    private static final String category    = "This is the category";

    /**
     * Most simple test just create the items and validate that they read back the same
     * @throws ParseException 
     */
    public void testSimpleCreateAndReadback() throws ParseException
    {
        ArrayList<String> categories = new ArrayList<String>();
        categories.add(category);
        FeedItem item = new FeedItem(nswTestDate1,title, link, guid, description, categories);
        
        String readDate = item.getDate();
        assertTrue(nswTestDate1.equals(readDate));
        
        String readTitle = item.getTitle();
        assertTrue(title.equals(readTitle));
        
        String readLink = item.getLink();
        assertTrue(link.equals(readLink));
        
        String readGuid = item.getGuid();
        assertTrue(readGuid.equals(guid));
        
        String readDescription = item.getDescription();
        assertTrue(description.equals(readDescription));
        
        String readCategory = item.getCategory();
        assertTrue(category.equals(readCategory));
        
        long rowId = item.getRowId();
        assertEquals(FeedDatabase.INVALIDROWID, rowId);
    }
    
    /**
     * Feed some null parameters in and make sure that they're not null when you read them back
     * @throws ParseException 
     */
    public void testNullParameters() throws ParseException
    {
        FeedItem item = new FeedItem(nswTestDate1,title, link, guid, null, null);
        
        String readDate = item.getDate();
        assertTrue(nswTestDate1.equals(readDate));
        
        String readTitle = item.getTitle();
        assertTrue(title.equals(readTitle));
        
        String readLink = item.getLink();
        assertTrue(link.equals(readLink));
        
        String readGuid = item.getGuid();
        assertTrue(readGuid.equals(guid));
        
        String readDescription = item.getDescription();
        assertTrue("".equals(readDescription));
        
        String readCategory = item.getCategory();
        assertTrue("".equals(readCategory));
    }

    /**
     * Validates the date parsing for a nsw data feed item
     * @throws ParseException
     */
    public void testNswDateParse() throws ParseException
    {
        FeedItem item = new FeedItem(nswTestDate1,title, link, guid, null, null);
        
        Calendar cal  = CalendarUtils.createCalendarFromDateAndFormat(item.getDate(), CalendarUtils.getDateFormatForState(Region.Nsw));
        int dayOfWeek  = cal.get(Calendar.DAY_OF_WEEK);
        assertEquals(Calendar.MONDAY, dayOfWeek);
        
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        assertEquals(24,dayOfMonth);
        
        int month = cal.get(Calendar.MONTH);
        assertEquals(Calendar.SEPTEMBER,month);
        
        int year  = cal.get(Calendar.YEAR);
        assertEquals(2012,year);
        
        int hour  = cal.get(Calendar.HOUR_OF_DAY);
        assertEquals(5,hour);
        
        int minute= cal.get(Calendar.MINUTE);
        assertEquals(53,minute);
        
        int seconds = cal.get(Calendar.SECOND);
        assertEquals(0,seconds);
    }
    
    /**
     * Validates the date parsing for a nsw data feed item with an invalid date
     */
    public void testInvalidDateParse()
    {
        final String nswMalformedDate1 = "24 Sep 1908"; // Date that is not of the expected format

        FeedItem item = new FeedItem(nswMalformedDate1,title, link, guid, null, null);
        Calendar cal  = CalendarUtils.createCalendarFromDateAndFormat(item.getDate(), CalendarUtils.getDateFormatForState(Region.Nsw));
        assertNotNull(cal);
        
        int year = cal.get(Calendar.YEAR);
        assertTrue(year != 1908);   // Year should not have been parsed from invalid date
    }
}
