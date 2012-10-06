package com.bluebottlesoftware.nationalparkclosures.TestData;

import java.util.List;

import junit.framework.TestCase;
import android.database.Cursor;

import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

public class TestUtils
{
    public static void matchDatasets(Cursor c, List<FeedItem> items)
    {
        TestCase.assertEquals(c.getCount(), items.size());
        List<FeedItem>cursorItems = FeedDatabase.getItemsForCursor(c);
        for(FeedItem item : cursorItems)
        {
            assertItemOnList(item,items);
        }
    }

    public static void assertItemOnList(FeedItem item, List<FeedItem> items)
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
        TestCase.assertEquals(true, bFound);
    }
    
    /**
     * Asserts that the list of items is in decreasing order. Cursor must have at least two entries
     * @param items
     */
    public static void assertListIsDecreasing(Cursor c)
    {
        TestCase.assertTrue(c.getCount() >= 2);
        FeedItem previousItem = null;
        int previousState = Region.Nsw;
        List<FeedItem> items = FeedDatabase.getItemsForCursor(c);
        c.moveToFirst();
        for(FeedItem item : items)
        {
            if(previousItem != null)
            {
                long prevDateAsMs = CalendarUtils.createCalendarFromDateAndFormat(previousItem.getDate(), CalendarUtils.getDateFormatForState(previousState)).getTimeInMillis();
                long dateInMs = CalendarUtils.createCalendarFromDateAndFormat(item.getDate(), CalendarUtils.getDateFormatForState(c.getInt(c.getColumnIndex(FeedDatabase.COLUMN_STATE)))).getTimeInMillis();
                TestCase.assertTrue(prevDateAsMs >= dateInMs);
            }
            previousItem = item;
            previousState= c.getInt(c.getColumnIndex(FeedDatabase.COLUMN_STATE));
            c.moveToNext();
        }
    }
}
