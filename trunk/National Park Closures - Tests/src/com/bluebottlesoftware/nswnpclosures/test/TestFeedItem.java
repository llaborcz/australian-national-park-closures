package com.bluebottlesoftware.nswnpclosures.test;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

import android.util.Log;
import junit.framework.TestCase;

public class TestFeedItem extends TestCase
{
    private static final String TAG = "TestFeedItem";
    private static final String date  = "2012 - 09 - 12";
    private static final String title = "this is the title";
    private static final String link  = "http://www.google.com";
    private static final String guid  = "234234-34534532-234234";
    private static final String description = "This is a description string";
    private static final String category    = "This is the category";

    protected void setUp() throws Exception
    {
        super.setUp();
        Log.d(TAG,"setUp");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        Log.d(TAG,"tearDown");
    }

    /**
     * Most simple test just create the items and validate that they read back the same
     */
    public void testSimpleCreateAndReadback()
    {
        FeedItem item = new FeedItem(date, title, link, guid, description, category);
        
        String readDate = item.getDate();
        assertTrue(date.equals(readDate));
        
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
    }
    
    /**
     * Feed some null parameters in and make sure that they're not null when you read them back
     */
    public void testNullParameters()
    {
        FeedItem item = new FeedItem(date, title, link, guid, null, null);
        
        String readDate = item.getDate();
        assertTrue(date.equals(readDate));
        
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
}
