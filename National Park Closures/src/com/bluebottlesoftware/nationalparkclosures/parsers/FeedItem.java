package com.bluebottlesoftware.nationalparkclosures.parsers;


/**
 * Represents an item in a data feed
 */
public class FeedItem
{
    private String m_title = new String();
    private String m_description = new String();
    private String m_link = new String();
    private String m_date = new String();
    private String m_guid = new String();
    private String m_category = new String();
    
    public FeedItem(String date, String title, String link, String guid,String description,String category)
    {
        m_date  = date;
        m_title = title;
        m_link  = link;
        m_guid  = guid;
        m_description = description;
        m_category = category;
    }

    public String getTitle()
    {
        return m_title;
    }

    public void setTitle(String title)
    {
        this.m_title = title;
    }

    public String getDescription()
    {
        return m_description;
    }

    public void setDescription(String description)
    {
        this.m_description = description;
    }

    public String getLink()
    {
        return m_link;
    }

    public void setLink(String link)
    {
        this.m_link = link;
    }

    public String getDate()
    {
        return m_date;
    }

    public void setDate(String date)
    {
        this.m_date = date;
    }

    public String getGuid()
    {
        return m_guid;
    }

    public void setGuid(String guid)
    {
        this.m_guid = guid;
    }

    public String getCategory()
    {
        return m_category;
    }

    public void setCategory(String category)
    {
        this.m_category = category;
    }
}
