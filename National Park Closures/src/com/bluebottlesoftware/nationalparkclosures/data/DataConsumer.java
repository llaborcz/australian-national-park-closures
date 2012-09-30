package com.bluebottlesoftware.nationalparkclosures.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

/**
 * interface that is responsible for consuming the contents of a data feed and writing it to the database 
 *
 */
public interface DataConsumer
{

    /**
     * Returns the list of feed items for the given feed
     * @param feed
     * @return
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    List<FeedItem> getFeedItemsForFeed(InputStream feed)
            throws XPathExpressionException, SAXException, IOException,
            ParserConfigurationException;
}
