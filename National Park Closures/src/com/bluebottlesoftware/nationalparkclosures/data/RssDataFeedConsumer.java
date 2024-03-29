package com.bluebottlesoftware.nationalparkclosures.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import android.database.sqlite.SQLiteDatabase;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedParser;


/**
 * Class that is responsible for updating the database with the information from the NSW national parks feed 
 */
class RssDataFeedConsumer implements DataConsumer
{
    /**
     * Creates the feed items that represents the entries in the given NSW datafeed
     * @param feed
     * @return
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Override
    public List<FeedItem> getFeedItemsForFeed(InputStream feed) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        FeedParser parser = FeedParser.createFromStream(feed);
        return parser.getFeedItems();
    }

    /**
     * Doesn't store the items in memory - rather writes directly to database
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Override
    public void writeFeedToDatabase(SQLiteDatabase db, InputStream feedStream,int region) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        FeedParser.writeToDatabase(feedStream, db, region);
    }
}
