package com.bluebottlesoftware.nationalparkclosures.parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.util.Log;

import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;


/**
 * Represents an item in a data feed
 */
public class FeedItem
{
    private static final String TAG = "FeedItem";
    public static final char CategorySeparatorChar = ',';
    private long   m_rowId = FeedDatabase.INVALIDROWID;
    private String m_title;
    private String m_description;
    private String m_link;
    private String m_date;
    private String m_guid;
    private String m_category;
    private String m_dateFormat;
    private Calendar m_calendar;
    private long     m_dateTimeInMs;
    
    public FeedItem(String date, String dateFormat,String title, String link, String guid,String description,List<String> categories)
    {
        m_date  = date == null ? "" : date;
        m_dateFormat = dateFormat == null ? "" : dateFormat;
        m_title = title == null ? "": title;
        m_link  = link == null ? "" : link;
        m_guid  = guid == null ? "" : guid;
        m_description = description == null ? "" : description;
        if(categories == null || categories.size() == 0)
        {
            m_category = "";
        }
        else
        {
            m_category = getCategoryString(categories);
        }
        m_calendar = createCalendarFromDateAndFormat(m_date,dateFormat);
        m_dateTimeInMs = m_calendar.getTime().getTime();
    }

    /**
     * Creates the categories string from the list of categories provided
     * @param categories
     * @return
     */
    private static String getCategoryString(List<String> categories)
    {
        StringBuilder builder = new StringBuilder();
        for(String category : categories)
        {
            builder.append(category);
            builder.append(CategorySeparatorChar);
        }
        return builder.substring(0, builder.length()-1);
    }

    public static List<String> getCategoriesFromString(String categoriesString)
    {
        ArrayList<String> categoryArray = new ArrayList<String>();
        String [] categories = categoriesString.split(new String(new char[]{CategorySeparatorChar}));
        for(String category : categories)
        {
            categoryArray.add(category);
        }
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
    public FeedItem(String date, String dateFormat,String title, String link, String guid,String description,List<String> categories,long rowId)
    {
        this(date,dateFormat,title,link,guid,description,categories);
        m_rowId = rowId;
    }
    
    
    /**
     * Returns a calendar object based on the date provided and the format of that date
     * @param date
     * @param dateFormat
     * @return
     * @throws ParseException 
     */
    private static Calendar createCalendarFromDateAndFormat(String itemDate,String itemDateFormat)
    {
        Calendar cal  = new GregorianCalendar();
        try
        {
            DateFormat dateFormat = new SimpleDateFormat(itemDateFormat);
            Date date = dateFormat.parse(itemDate);
            cal.setTime(date);
        }   
        catch(ParseException e)
        {
            Log.e(TAG,"Caught ParseException while processing date");
        }
        
        return cal;
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
    
    public String getDateFormat()
    {
        return m_dateFormat;
    }

    public long getDateAsms()
    {
        return m_dateTimeInMs;
    }
    
    /**
     * Returns the calendar object that defines the date associated with this entry
     * @return
     */
    public Calendar getCalendar()
    {
        return m_calendar;
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
        StringBuilder sb = new StringBuilder(m_date.length() + m_guid.length() + m_category.length() + m_link.length() + 8);
        sb.append(m_title).append(' ').append(m_date).append(' ').append(m_link).append(' ').append(m_guid).append(' ').append(m_category);
        return sb.toString();
    }

    /**
     * We're not implementing equals since I don't want to implement hashCode
     * @param other
     * @return
     */
    public boolean sameAs(FeedItem other)
    {
        boolean bSame = m_title.equals(other.getTitle()) &&
                        m_link.equals(other.getLink()) &&
                        m_date.equals(other.getDate()) && 
                        m_category.equals(other.getCategory()) &&
                        m_description.equals(other.getDescription()) && 
                        m_guid.equals(other.getGuid());
        return bSame;
    }
}
