package com.bluebottlesoftware.nationalparkclosures.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nationalparkclosures.parsers.NswFeedParser;


/**
 * Class that is responsible for updating the database with the information from the NSW national parks feed 
 */
class NswDataConsumer implements DataConsumer
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
        NswFeedParser parser = NswFeedParser.createFromStream(feed);
        return parser.getFeedItems();
    }
}
