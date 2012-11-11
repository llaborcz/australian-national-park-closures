package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class GeoNamespaceContext implements NamespaceContext
{
    private static final String prefixGeo    = "geo";
    private static final String prefixGeoUri = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    private static final String prefixGeoRss = "georss";
    private static final String prefixGeoRssUri = "http://www.georss.org/georss";
    
    @Override
    public String getNamespaceURI(String prefix)
    {
        if(prefix == null)
        {
            throw new IllegalArgumentException("null prefix provided");
        }
        String uri;
   
        if(prefixGeo.equals(prefix))
        {
            uri = prefixGeoUri;
        }
        else if(prefixGeoRss.equals(prefix))
        {
            uri = prefixGeoRssUri;
        }
        else
        {
            uri = XMLConstants.NULL_NS_URI;
        }
        return uri;
    }

    @Override
    public String getPrefix(String namespaceURI)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
