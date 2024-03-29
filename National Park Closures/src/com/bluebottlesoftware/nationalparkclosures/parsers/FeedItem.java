package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;


/**
 * Represents an item in a data feed
 */
public class FeedItem
{
    public static final char CategorySeparatorChar = ',';
    private static final int ARRAY_LIST_INITIAL_SIZE = 128;

    private long   m_rowId = FeedDatabase.INVALIDROWID;
    private String m_title;
    private String m_description;
    private String m_link;
    private String m_date;
    private String m_guid;
    private String m_category;
    private String m_geoLat;
    private String m_geoLong;
    
    public FeedItem(String date, String title, String link, String guid,String description,List<String> categories,String geoLat,String geoLong)
    {
        m_date  = date == null ? "" : date;
        m_title = title == null ? "": title;
        m_link  = link == null ? "" : link;
        m_guid  = guid == null ? "" : guid;
        m_description = description == null ? "" : description;
        m_geoLat  = geoLat;
        m_geoLong = geoLong;
        
        if(categories == null || categories.size() == 0)
        {
            m_category = "";
        }
        else
        {
            m_category = getCategoryString(categories);
        }
    }

    /**
     * Creates the categories string from the list of categories provided
     * @param categories
     * @return
     */
    private static String getCategoryString(List<String> categories)
    {
        StringBuilder builder = new StringBuilder(64);
        for(String category : categories)
        {
            builder.append(category);
            builder.append(CategorySeparatorChar);
        }
        return builder.substring(0, builder.length()-1);
    }

    public static List<String> getCategoriesFromString(String categoriesString)
    {
        ArrayList<String> categoryArray = new ArrayList<String>(ARRAY_LIST_INITIAL_SIZE);
        String [] categories = categoriesString.split(new String(new char[]{CategorySeparatorChar}));
        Collections.addAll(categoryArray, categories);
        return categoryArray;
    }
    
    /**
     * Alternate constructor for when this FeedItem is read from the database and has an associated rowid
     * @param date
     * @param title
     * @param link
     * @param guid
     * @param description
     * @param category
     * @param rowId
     * @throws ParseException 
     */
    public FeedItem(String date,String title, String link, String guid,String description,List<String> categories,String geoLat,String geoLong,long rowId)
    {
        this(date,title,link,guid,description,categories,geoLat,geoLong);
        m_rowId = rowId;
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

    public String getLatitude()
    {
        return m_geoLat;
    }
    
    public String getLongtitude()
    {
        return m_geoLong;
    }
    
    /**
     * Returns rowid in database or -1 if this wasn't read from the database
     * @return
     */
    public long getRowId()
    {
        return m_rowId;
    }
    
    @Override
    public String toString()
    {
        return m_title + ' ' + m_date + ' ' + m_link + ' ' + m_guid + ' ' + m_category + ' ' + m_geoLat + ' ' + m_geoLong;
    }

    /**
     * We're not implementing equals since I don't want to implement hashCode
     * @param other
     * @return
     */
    public boolean sameAs(FeedItem other)
    {
        boolean bSame = m_title.equals(other.m_title) &&
                        m_link.equals(other.m_link) &&
                        m_date.equals(other.m_date) &&
                        m_category.equals(other.m_category) &&
                        m_description.equals(other.m_description) &&
                        m_guid.equals(other.m_guid);
        return bSame;
    }
}
