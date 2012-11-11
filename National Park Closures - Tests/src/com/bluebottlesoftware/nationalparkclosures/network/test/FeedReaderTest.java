package com.bluebottlesoftware.nationalparkclosures.network.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestUtils;
import com.bluebottlesoftware.nationalparkclosures.data.FeedReader;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.database.test.ClosureDatabaseTest;
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
            reader = FeedReader.createInstance(Region.Nsw+200);
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
        innerTestReaderCreation(Region.Nsw);
    }
    
    public void testQldReaderCreation() throws MalformedURLException, IllegalArgumentException
    {
        innerTestReaderCreation(Region.Qld);
    }
    
    public void testWaReaderCreation() throws MalformedURLException, IllegalArgumentException
    {
        innerTestReaderCreation(Region.WaFireIncidents);
    }
    
    private void innerTestReaderCreation(int region) throws MalformedURLException, IllegalArgumentException
    {
        FeedReader reader = null;
        reader = FeedReader.createInstance(region);
        assertFalse(reader == null);
    }
    
    public void testQldReaderFetch() throws IllegalArgumentException, XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        innerTestRegionFetch(Region.Qld);
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
        innerTestRegionFetch(Region.Nsw);
    }
    
    public void testWaReaderFetch() throws IllegalArgumentException, XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        innerTestRegionFetch(Region.WaFireIncidents);
    }
    
    private void innerTestRegionFetch(int region) throws IllegalArgumentException, XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        FeedReader reader = FeedReader.createInstance(region);
        List<FeedItem> items = reader.getFeedItems();
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
    public void testReadFromNetworkAndWriteToDatbaseNsw() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        innerTestReadFromNetworkAndWriteToDatabase(Region.Nsw);
    }
    
    public void testReadFromNetworkAndWriteToDatbaseQld() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        innerTestReadFromNetworkAndWriteToDatabase(Region.Qld);
    }
    
    public void testReadFromNetworkAndWriteToDatbaseWa() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        innerTestReadFromNetworkAndWriteToDatabase(Region.WaFireIncidents);
    }
    
    private void innerTestReadFromNetworkAndWriteToDatabase(int region) throws IllegalArgumentException, XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = ClosureDatabaseTest.getEmptyDatabase(this); // We've got an empty database
        FeedReader reader = FeedReader.createInstance(region);
        List<FeedItem> items = reader.getFeedItems();
        
        FeedDatabase.updateDatabaseWithTransaction(db, items, region);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, region);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        db.close();        
    }
}
