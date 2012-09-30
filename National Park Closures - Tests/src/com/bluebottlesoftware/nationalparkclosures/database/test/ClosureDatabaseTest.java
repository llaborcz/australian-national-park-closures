package com.bluebottlesoftware.nationalparkclosures.database.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestConstants;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumer;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumerFactory;
import com.bluebottlesoftware.nationalparkclosures.data.State;
import com.bluebottlesoftware.nationalparkclosures.database.ClosureDatabase;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nswnpclosures.test.R;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityTestCase;
import android.util.Log;

public class ClosureDatabaseTest extends ActivityTestCase
{
    private SQLiteDatabase getEmptyDatabase()
    {
        // Empty the database
        DatabaseHelper helper = new DatabaseHelper(getActivity());  // TODO Is the getActivity call right here?
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.dropAllTables(db);
        helper.onCreate(db);
        Cursor c = ClosureDatabase.getItemsForStateSortedByDate(db, State.Nsw);
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
        SQLiteDatabase db = getEmptyDatabase();
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(State.Nsw);
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries,items.size());
        ClosureDatabase.writeFeedItemsToDatabase(db, items, State.Nsw);
        Cursor c = ClosureDatabase.getItemsForStateSortedByDate(db, State.Nsw);
        
        // Now make sure that what we read out corresponds exactly to  the contents of the list
        matchDatasets(c,items);
        
        // Make sure that the items are in fact sorted by decreasing date order
        assertListIsDecreasing(c);
        c.close();
    }

    /**
     * Asserts that the list of items is in decreasing order. Cursor must have at least two entries
     * @param items
     */
    private static void assertListIsDecreasing(Cursor c)
    {
        assert(c.getCount() >= 2);
        FeedItem previousItem = null;
        List<FeedItem> items = ClosureDatabase.getItemsForCursor(c);
        for(FeedItem item : items)
        {
            if(previousItem != null)
            {
                long prevDateAsMs = item.getDateAsms();
                long dateInMs = item.getDateAsms();
                assert(prevDateAsMs >= dateInMs);
            }
            previousItem = item;
        }
    }

    private static void matchDatasets(Cursor c, List<FeedItem> items)
    {
        assertEquals(c.getCount(), items.size());
        List<FeedItem>cursorItems = ClosureDatabase.getItemsForCursor(c);
        for(FeedItem item : cursorItems)
        {
            assertItemOnList(item,items);
        }
    }

    private static void assertItemOnList(FeedItem item, List<FeedItem> items)
    {
        boolean bFound = false;
        for(FeedItem compare : items)
        {
            if(compare.sameAs(item))
            {
                bFound = true;
                break;
            }
        }
        Log.e("assertItemOnList",item.toString() + " not found in list");
        assertEquals(true, bFound);
    }
}
