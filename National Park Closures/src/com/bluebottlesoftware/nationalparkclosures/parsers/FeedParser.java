package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
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
    
    private static final String LeadingSlash = "/";
    
    private ArrayList<FeedItem> m_items = new ArrayList<FeedItem>();    // Stores our list of nodes
    
    private XPathExpression mItemQuery;
    private XPathExpression mDateQuery;
    private XPathExpression mTitleQuery;
    private XPathExpression mLinkQuery;
    private XPathExpression mGuidQuery;
    private XPathExpression mCategoryQuery;
    private XPathExpression mDescriptionQuery;
    private XPathExpression mGeoLatQuery;
    private XPathExpression mGeoLongQuery;
    private XPathExpression mGeoRssPointQuery;
    private XPathExpression mGeoRssCollectionPointQuery;
    
    private void compileXpathExpressions(XPath xpath) throws XPathExpressionException
    {
        mItemQuery = xpath.compile(ItemQuery);
        mDateQuery = xpath.compile(DateQuery);
        mTitleQuery = xpath.compile(TitleQuery);
        mLinkQuery  = xpath.compile(LinkQuery);
        mGuidQuery  = xpath.compile(GuidQuery);
        mCategoryQuery = xpath.compile(CategoryQuery);
        mDescriptionQuery = xpath.compile(DescriptionQuery);
        mGeoLatQuery = xpath.compile(GeoLatQuery);
        mGeoLongQuery= xpath.compile(GeoLongQuery);
        mGeoRssPointQuery = xpath.compile(GeoRssPointQuery);
        mGeoRssCollectionPointQuery = xpath.compile(GeoRssCollectionPointQuery);
    }
    
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
        compileXpathExpressions(xpath);
        NodeList items = (NodeList) mItemQuery.evaluate(xmlDocument,XPathConstants.NODESET);
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
        String geoLat = null;
        String geoLong= null;
        String date  = (String) mDateQuery.evaluate(node,XPathConstants.STRING);
        String title = (String) mTitleQuery.evaluate(node,XPathConstants.STRING);
        String link  = (String) mLinkQuery.evaluate(node,XPathConstants.STRING);
        String guid  = (String) mGuidQuery.evaluate(node,XPathConstants.STRING);
        String description = (String) mDescriptionQuery.evaluate(node,XPathConstants.STRING);
        String point   = (String) mGeoRssPointQuery.evaluate(node,XPathConstants.STRING);
        if(TextUtils.isEmpty(point))
        {
            point = (String)mGeoRssCollectionPointQuery.evaluate(node,XPathConstants.STRING);
        }
        
        if(TextUtils.isEmpty(point))
        {
            geoLat  = (String) mGeoLatQuery.evaluate(node,XPathConstants.STRING);
            geoLong = (String) mGeoLongQuery.evaluate(node,XPathConstants.STRING);
        }
        else
        {
            // We've got a geo rss point
            String [] latlong = point.split(" ");
            if(latlong.length == 2)
            {
                geoLat = latlong[0];
                geoLong= latlong[1];
            }
        }
        
        NodeList categories = (NodeList) mCategoryQuery.evaluate(node,XPathConstants.NODESET);
        ArrayList<String> categoryArray = new ArrayList<String>();
        for(int i=0;i<categories.getLength();i++)
        {
            Node catNode = categories.item(i);
            String cat = catNode.getTextContent().trim();
            if(cat.startsWith(LeadingSlash))
            {
                cat = cat.substring(1);
            }
            categoryArray.add(cat);
        }
        
        FeedItem item = new FeedItem(date,title.trim(),link,guid,description,categoryArray,geoLat,geoLong);
        Log.d("item",item.toString());
        return item;
    }
}
