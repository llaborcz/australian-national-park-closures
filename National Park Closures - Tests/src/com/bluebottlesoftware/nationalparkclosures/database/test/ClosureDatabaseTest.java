package com.bluebottlesoftware.nationalparkclosures.database.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestConstants;
import com.bluebottlesoftware.nationalparkclosures.TestData.TestUtils;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumer;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumerFactory;
import com.bluebottlesoftware.nationalparkclosures.data.State;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nswnpclosures.test.R;

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
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
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
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(State.Nsw);
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        FeedDatabase.writeFeedItemsToDatabase(db, items, State.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
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
    public void testInsertIntoDatabaseWithTransaction() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        SQLiteDatabase db = getEmptyDatabase(this);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(State.Nsw);
        InputStream  stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, State.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
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
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(State.Nsw);
        InputStream  stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        FeedDatabase.updateDatabaseWithTransaction(db, items, State.Nsw);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        
        // Now write all the entries again and repeat the tests the results should be the same
        consumer = DataConsumerFactory.createDataConsumer(State.Nsw);
        stream   = getInstrumentation().getContext().getResources().openRawResource(R.raw.nsw_78);
        items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValid78Entries,items.size());
        
        
        FeedDatabase.updateDatabaseWithTransaction(db, items, State.Nsw);
        c = FeedDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        TestUtils.matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        TestUtils.assertListIsDecreasing(c);
        c.close();
        
        db.close();
        
    }

    
}
