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
import com.bluebottlesoftware.parkclosures.test.R;

import android.test.ActivityTestCase;
import android.text.TextUtils;

public class RssDataFeedConsumerTest extends ActivityTestCase
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
        assertTrue(TestConstants.SingleEntryCategory.equals(category));
    }
    
    public void testNullLatLong() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.nswfeed_singleitem);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());    
        FeedItem item = items.get(0);
        String lat = item.getLatitude();
        String longtitude = item.getLongtitude();
        assertTrue(TextUtils.isEmpty(lat));
        assertTrue(TextUtils.isEmpty(longtitude));
    }
    
    public void testTwoCategories() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.twocategories);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Nsw);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(1, items.size());    
        FeedItem item = items.get(0);
        String category  = item.getCategory();
        assertTrue(TestConstants.TwoCategories.equals(category));        
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
    
    public void testQldFeedCountCorrect() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.qldparkalerts);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.Qld);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        assertEquals(TestConstants.NumQldValidEntries, items.size());
    }
    
    public void testWaFeedWithGeoInformation() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        InputStream stream = this.getInstrumentation().getContext().getResources().openRawResource(R.raw.wafirealerts);
        DataConsumer consumer = DataConsumerFactory.createDataConsumer(Region.WaFireIncidents);
        List<FeedItem> items  = consumer.getFeedItemsForFeed(stream);
        FeedItem item = items.get(0);
        String latitude   = item.getLatitude();
        String longtitude = item.getLongtitude();
        
        assertTrue(!TextUtils.isEmpty(longtitude));
        assertTrue(!TextUtils.isEmpty(latitude));
    }
}
