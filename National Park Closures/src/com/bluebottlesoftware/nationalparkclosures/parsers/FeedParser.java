package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.TextUtils;
import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.Util.XmlUtils;

/**
 * XPath based parser for NSW national park feed
 */
public class FeedParser
{
    private static final String ItemQuery = "/rss/channel/item";    // Returns back all of the items
    private static final String DateQuery  = "pubDate";
    private static final String TitleQuery = "title";
    private static final String LinkQuery  = "link";
    private static final String GuidQuery  = "guid";
    private static final String CategoryQuery = "category";
    private static final String DescriptionQuery = "description";
    private static final String GeoLatQuery  = "lat";
    private static final String GeoLongQuery = "long";
    private static final String GeoRssPointQuery = "point";
    private static final String GeoRssCollectionPointQuery = "collection/point";
    private ArrayList<FeedItem> m_items = new ArrayList<FeedItem>();    // Stores our list of nodes
    
    /**
     * Prevent instantiation
     */
    private FeedParser()
    {
    }
    
    /**
     * Create an instance of the parser based on an input stream
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public static FeedParser createFromStream(InputStream stream) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException
    {
        FeedParser parser = new FeedParser();
        Document xmlDocument = XmlUtils.readXml(stream);
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new GeoNamespaceContext());
        parser.parse(xmlDocument,xpath);
        return parser;
    }
    
    /**
     * Returns all of the feed items that were returned in the stream
     * @return
     */
    public List<FeedItem> getFeedItems()
    {
        return m_items;
    }

    /**
     * Parses the document into items
     * @param xmlDocument
     * @param xpath
     * @throws XPathExpressionException 
     */
    private void parse(Document xmlDocument, XPath xpath) throws XPathExpressionException
    {
        NodeList items = (NodeList) xpath.evaluate(ItemQuery, xmlDocument,XPathConstants.NODESET);
        for(int item = 0;item < items.getLength();item++)
        {
            m_items.add(createItemFromXPath(items.item(item),xpath));
        }
    }

    /**
     * Creates a FeedItem from the given XML node
     * @param item
     * @return
     * @throws XPathExpressionException 
     * @throws ParseException 
     */
    private FeedItem createItemFromXPath(Node node,XPath xpath) throws XPathExpressionException
    {
        String date  = (String) xpath.evaluate(DateQuery, node,XPathConstants.STRING);
        String title = (String) xpath.evaluate(TitleQuery, node,XPathConstants.STRING);
        String link  = (String) xpath.evaluate(LinkQuery, node,XPathConstants.STRING);
        String guid  = (String) xpath.evaluate(GuidQuery, node,XPathConstants.STRING);
        String description = (String) xpath.evaluate(DescriptionQuery, node,XPathConstants.STRING);
        String geoLat  = (String) xpath.evaluate(GeoLatQuery, node,XPathConstants.STRING);
        String geoLong = (String) xpath.evaluate(GeoLongQuery, node,XPathConstants.STRING);
        String point   = (String) xpath.evaluate(GeoRssPointQuery,node,XPathConstants.STRING);
        if(TextUtils.isEmpty(point))
        {
            point = (String)xpath.evaluate(GeoRssCollectionPointQuery, node,XPathConstants.STRING);
        }
        
        if(!TextUtils.isEmpty(point))
        {
            // We've got a geo rss point
            String [] latlong = point.split(" ");
            if(latlong.length == 2)
            {
                geoLat = latlong[0];
                geoLong= latlong[1];
            }
        }
        
        NodeList categories = (NodeList) xpath.evaluate(CategoryQuery, node,XPathConstants.NODESET);
        ArrayList<String> categoryArray = new ArrayList<String>();
        for(int i=0;i<categories.getLength();i++)
        {
            Node catNode = categories.item(i);
            String cat = catNode.getTextContent();
            categoryArray.add(cat);
        }
        
        FeedItem item = new FeedItem(date,title.trim(),link,guid,description,categoryArray,geoLat,geoLong);
        Log.d("item",item.toString());
        return item;
    }
}
