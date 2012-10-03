package com.bluebottlesoftware.nationalparkclosures.network.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestUtils;
import com.bluebottlesoftware.nationalparkclosures.data.State;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.database.test.ClosureDatabaseTest;
import com.bluebottlesoftware.nationalparkclosures.network.FeedReader;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityTestCase;
import android.util.Log;

/**
 * Test cases for reading from the network
 */
public class FeedReaderTest extends ActivityTestCase
{
    /**
     * Requests a reader for a non existent state
     * @throws MalformedURLException
     */
    public void testInvalidStateRequest() throws MalformedURLException
    {
        boolean bException = false;
        FeedReader reader  = null;
        try
        {
            reader = FeedReader.createInstance(State.Qld);
        }
        catch(IllegalArgumentException e)
        {
            bException = true;
        }        
        assertEquals(null, reader);
        assertTrue(bException);
    }
    
    /**
     * Requests a reader for NSW which is valid
     * @throws MalformedURLException 
     */
    public void testNswReaderCreation() throws MalformedURLException
    {
        FeedReader reader = null;
        reader = FeedReader.createInstance(State.Nsw);
        assertFalse(reader == null);
    }
    
    /**
     * Goes and gets the feed for NSW 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public void testNswReaderFetch() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        FeedReader reader = FeedReader.createInstance(State.Nsw);
        List<FeedItem> items = reader.connectAndGetFeedItems();
        for(FeedItem item : items)
        {
            Log.d("Fetch from network",item.toString());
        }
    }
    
    /**
     * Reads the feed from the network, writes to the database and validates the databae integrity (i.e. that what is in the database is what was read)
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public void testReadFromNetworkAndWriteToDatbase() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = ClosureDatabaseTest.getEmptyDatabase(this); // We've got an empty database
        FeedReader reader = FeedReader.createInstance(State.Nsw);
        List<FeedItem> items = reader.connectAndGetFeedItems();
        
        FeedDatabase.updateDatabaseWithTransaction(db, items, State.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        db.close();        
    }
}
