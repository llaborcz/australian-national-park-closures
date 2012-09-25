package com.bluebottlesoftware.nationalparkclosures.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlUtils
{
    public static Document readXml(InputStream is,boolean bNamespaceAware) throws SAXException, IOException,ParserConfigurationException 
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(bNamespaceAware);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse(is);
    }
    
    public static Document readXml(InputStream is) throws SAXException, IOException, ParserConfigurationException
    {
        return readXml(is,false);
    }
    
    public static String getAsXmlString(Document doc) throws TransformerException, IllegalStateException, ParserConfigurationException
    {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans;
        trans = transfac.newTransformer();
        
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter sw     = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source    = new DOMSource(doc);
        trans.transform(source, result);
        return sw.toString();
    } 
}