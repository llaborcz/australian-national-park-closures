package com.bluebottlesoftware.nationalparkclosures.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


import com.bluebottlesoftware.nationalparkclosures.data.DataConsumer;
import com.bluebottlesoftware.nationalparkclosures.data.DataConsumerFactory;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

/**
 * Class that will get a feed from the network and read the feed.
 * Obtain an instance by calling getInstance passing in the appropriate state 
 */
public class FeedReader
{
    
    private URL mUrl;
    private DataConsumer mDataConsumer;
    
    /**
     * Private constructor that takes the URL of the feed that needs to be consumed
     * @param url
     */
    private FeedReader(URL url, DataConsumer dataConsumer)
    {
        mUrl = url;
        mDataConsumer = dataConsumer;
    }
    
    /**
     * Returns a new instance of a FeedReader that is used to fetch a particular instance
     * @param state
     * @return
     * @throws MalformedURLException 
     */
    public static FeedReader createInstance(int state) throws MalformedURLException, IllegalArgumentException
    {
        FeedReader reader = null;
        switch(state)
        {
        case Region.Nsw:
            reader = new FeedReader(new URL(Region.getFeedForStateOrRegion(state)), DataConsumerFactory.createDataConsumer(state));
            break;
            
        default:
            throw new IllegalArgumentException("State " + state + " not supported");
        }
        
        return reader;
    }
    
    /**
     * Creates the connection and returns the list of feeditems that's present in the feed
     * This is a blocking call and must not be called on the main thread
     * @return
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public List<FeedItem> connectAndGetFeedItems() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        List<FeedItem> items = null;
        
        if(null == mUrl)
        {
            throw new IllegalStateException("mUrl is null");
        }
        
        if(null == mDataConsumer)
        {
            throw new IllegalStateException("mDataConsumer is null");
        }
        
        InputStream feedStream = getFeedStream(mUrl);
        items = mDataConsumer.getFeedItemsForFeed(feedStream);
        feedStream.close();
        return items;
    }

    /**
     * Gets an input stream for the given URL
     * @return
     * @throws IOException 
     */
    private static InputStream getFeedStream(URL url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        return connection.getInputStream();
    }
}
