package com.bluebottlesoftware.nswnpclosures.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import android.test.ActivityTestCase;
import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nationalparkclosures.parsers.NswFeedParser;

/**
 * Contains various test cases for the NSW data feed parser
 */
public class NswParserTests extends ActivityTestCase
{
    private static final int NswItemCount = 79; // As returned by xv feed renderer extension in chrome
    
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testBasicParse() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        NswFeedParser parser = NswFeedParser.createFromStream(stream);
        List<FeedItem> items = parser.getFeedItems();
        for(FeedItem item : items)
        {
            String date = item.getDate();
            assertNotNull(date);
            
            String category = item.getCategory();
            assertNotNull(category);
            
            String description = item.getDescription();
            assertNotNull(description);
            
            String guid = item.getGuid();
            assertNotNull(guid);
            
            String link = item.getLink();
            assertNotNull(link);
            
            String title = item.getTitle();
            assertNotNull(title);
            
            Log.d("item",item.toString());
        }
        assertEquals(NswItemCount,items.size());
    }
    
    public void testParseOfCorruptFile()
    {
        
    }
    
    public void testParseOfEmptyData()
    {
        
    }
}
