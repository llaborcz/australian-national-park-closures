package com.bluebottlesoftware.nationalparkclosures.data.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.TestData.TestConstants;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumer;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumerFactory;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nswnpclosures.test.R;

import android.test.ActivityTestCase;

public class NswDataConsumerTest extends ActivityTestCase
{
    /**
     * Tests that the consumer returns a list of items corresponding to the data set
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void testBasicFeed() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumNswValidEntries, items.size());
    }
    
    /**
     * Tests that the consumer can handle an empty list
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void testEmptyFeed() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_empty);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(0, items.size());   
    }
    
    /**
     * Tests that the consumer returns an appropriate single item entry
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void testSingleItemFeed() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());    
        FeedItem item = items.get(0);
        String category  = item.getCategory();
        assert(TestConstants.SingleEntryCategory.equals(category));
    }
    
    public void testTwoCategories() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.twocategories);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());    
        FeedItem item = items.get(0);
        String category  = item.getCategory();
        assert(TestConstants.TwoCategories.equals(category));        
    }
    
    public void testSingleItemMissingElements() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem_missingelements);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());    
        
        FeedItem item = items.get(0);
        String value = item.getGuid();
        assertEquals("", value);
        value = item.getCategory();
        assertNotSame("",value);
    }
}
