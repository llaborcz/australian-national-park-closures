package com.bluebottlesoftware.nationalparkclosures.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import android.database.sqlite.SQLiteDatabase;

import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

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

    void writeFeedToDatabase(SQLiteDatabase db, InputStream feedStream,int region) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException;
}
