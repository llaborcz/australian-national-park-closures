package com.bluebottlesoftware.nationalparkclosures.database.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestConstants;
import com.bluebottlesoftware.nationalparkclosures.TestData.TestUtils;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumer;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumerFactory;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.parkclosures.test.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityTestCase;

public class ClosureDatabaseTest extends ActivityTestCase
{
    public static SQLiteDatabase getEmptyDatabase(ActivityTestCase activityTestCase)
    {
        // Empty the database
        Context context = activityTestCase.getInstrumentation().getTargetContext().getApplicationContext();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.dropAllTables(db);
        helper.onCreate(db);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        assertEquals(0,c.getCount());
        c.close();
        return db;
    }
    
    /**
     * Tests the flow of getting a datastream to getting that stream into the database
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public void testInsertToEmptyDatabase() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        FeedDatabase.writeFeedItemsToDatabase(db, items, Region.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        db.close();
    }
    
    /**
     * Tests the flow of insertion into the database using a transaction
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void testInsertIntoDatabaseWithTransactionNsw() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream  stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        innerTestInsertIntoDatabaseWithTransaction(stream,Region.Nsw, TestConstants.NumNswValidEntries);
    }
    
    public void testInsertIntoDatabaseWithTransactionQld() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream  stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.qldparkalerts);
        innerTestInsertIntoDatabaseWithTransaction(stream,Region.Qld, TestConstants.NumQldValidEntries);
    }
    
    private void innerTestInsertIntoDatabaseWithTransaction(InputStream stream, int region,int size) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(region);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(size,items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, region);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, region);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        db.close();
    }
    
    public void testInsertThenUpdateWithTransactions() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        InputStream  stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, Region.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        
        // Now write all the entries again and repeat the tests the results should be the same
        consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nsw_78);
        items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValid78Entries,items.size());
        
        
        FeedDatabase.updateDatabaseWithTransaction(db, items, Region.Nsw);
        c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        
        db.close();
    }

    /**
     * Tests the readback of a description from the database that was added in
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void testStringReadPositive() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        
        // First we write the single entry to the database
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        InputStream stream = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem);
        List<FeedItem> items = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, Region.Nsw);
        
        // Now we need to read the single item back so that we can get its row id
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        assertEquals(1, c.getCount());
        c.moveToFirst();
        int rowId = c.getInt(c.getColumnIndex(FeedDatabase.COLUMN_ID));
        c.close();
        
        // Now read just the description and let's compare it to the entry that we read from the raw feed
        String description = FeedDatabase.getDescriptionForEntry(db, rowId);
        assertTrue(description.equals(items.get(0).getDescription()));
        
        // Now we do the same for the title
        String title = FeedDatabase.getTitleForEntry(db, rowId);
        assertTrue(title.equals(items.get(0).getTitle()));
        
        // And the same for the link
        String link = FeedDatabase.getLinkForEntry(db, rowId);
        assertTrue(link.equals(items.get(0).getLink()));
        
        db.close();
    }
    
    /**
     * Validates that the description lookup returns null if the entry isn't found
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public void testDescriptionReadNegative() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        
        // First we write the single entry to the database
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        InputStream stream = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem);
        List<FeedItem> items = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, Region.Nsw);
        
        // Now we need to read the single item back so that we can get its row id
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        assertEquals(1, c.getCount());
        c.moveToFirst();
        int rowId = c.getInt(c.getColumnIndex(FeedDatabase.COLUMN_ID));
        c.close();
        
        // Now read just the description and let's compare it to the entry that we read from the raw feed
        String description = FeedDatabase.getDescriptionForEntry(db, rowId+234234);
        assertEquals(null, description);
        
        // Now we do the same for the title
        String title = FeedDatabase.getTitleForEntry(db, rowId+234);
        assertEquals(null, title);
        
        db.close();
    }
    
    public void testSingleRegionTimeUpdate()
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        
        long testTime = 400;
        FeedDatabase.setLastUpdateTimeForRegion(db, Region.Nsw, testTime);
        
        long readBack = FeedDatabase.getLastUpdateTimeForRegion(db, Region.Nsw);
        assertEquals(testTime, readBack);
        
        db.close();
    }
    
    public void testMultipleUpdates()
    {
    SQLiteDatabase db = getEmptyDatabase(this);
        
        long testTime = 400;
        FeedDatabase.setLastUpdateTimeForRegion(db, Region.Nsw, testTime);
        
        long readBack = FeedDatabase.getLastUpdateTimeForRegion(db, Region.Nsw);
        assertEquals(testTime, readBack);
        
        testTime = 500;
        FeedDatabase.setLastUpdateTimeForRegion(db, Region.Nsw, testTime);
        readBack = FeedDatabase.getLastUpdateTimeForRegion(db, Region.Nsw);
        assertEquals(testTime, readBack);
    }
}
