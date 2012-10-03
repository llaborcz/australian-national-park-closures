package com.bluebottlesoftware.nswnpclosures.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import android.test.ActivityTestCase;
import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestConstants;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nationalparkclosures.parsers.NswFeedParser;

/**
 * Contains various test cases for the NSW data feed parser
 */
public class NswParserTests extends ActivityTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Basic parser test that will just ensure that the content was parsed correctly
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
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
        assertEquals(TestConstants.NumNswValidEntries,items.size());
    }
    
    public void testParseOfCorruptFile()
    {
        
    }
    
    /**
     * Parse of a file that contains no items
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public void testNoItemsFile() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_empty);
        NswFeedParser parser = NswFeedParser.createFromStream(stream);
        List<FeedItem> items = parser.getFeedItems();
        assertEquals(0, items.size());
    }
    
    public void testSingleItemFile() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem);
        NswFeedParser parser = NswFeedParser.createFromStream(stream);
        List<FeedItem> items = parser.getFeedItems();
        assertEquals(1, items.size());        
    }
    
    public void testSingleItemMissingItemElementsFile() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem_missingelements);
        NswFeedParser parser = NswFeedParser.createFromStream(stream);
        List<FeedItem> items = parser.getFeedItems();
        assertEquals(1, items.size());
        FeedItem item = items.get(0);
        String value = item.getGuid();
        assertEquals("", value);
        value = item.getCategory();
        assertNotSame("",value);
    }
}