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

import com.bluebottlesoftware.nationalparkclosures.Util.XmlUtils;

/**
 * XPath based parser for NSW national park feed
 */
public class NswFeedParser
{
    private static final String ItemQuery = "/rss/channel/item";    // Returns back all of the items
    private static final String Date  = "pubDate";
    private static final String Title = "title";
    private static final String Link  = "link";
    private static final String Guid  = "guid";
    private static final String Category = "category";
    private static final String Description = "description";
    
    private ArrayList<FeedItem> m_items = new ArrayList<FeedItem>();    // Stores our list of nodes
    
    /**
     * Prevent instantiation
     */
    private NswFeedParser()
    {
    }
    
    /**
     * Create an instance of the parser based on an input stream
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public static NswFeedParser createFromStream(InputStream stream) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException
    {
        NswFeedParser parser = new NswFeedParser();
        Document xmlDocument = XmlUtils.readXml(stream);
        XPath xpath = XPathFactory.newInstance().newXPath();
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
        String date  = (String) xpath.evaluate(Date, node,XPathConstants.STRING);
        String title = (String) xpath.evaluate(Title, node,XPathConstants.STRING);
        String link  = (String) xpath.evaluate(Link, node,XPathConstants.STRING);
        String guid  = (String) xpath.evaluate(Guid, node,XPathConstants.STRING);
        String description = (String) xpath.evaluate(Description, node,XPathConstants.STRING);
        NodeList categories = (NodeList) xpath.evaluate(Category, node,XPathConstants.NODESET);
        ArrayList<String> categoryArray = new ArrayList<String>();
        for(int i=0;i<categories.getLength();i++)
        {
            Node catNode = categories.item(i);
            String cat = catNode.getTextContent();
            categoryArray.add(cat);
        }
        
        FeedItem item = new FeedItem(date,DateFormats.NswDateFormat,title,link,guid,description,categoryArray);
        return item;
    }
}
