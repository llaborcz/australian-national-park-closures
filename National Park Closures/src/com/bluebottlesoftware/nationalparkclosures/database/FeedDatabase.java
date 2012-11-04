package com.bluebottlesoftware.nationalparkclosures.database;

import java.util.ArrayList;
import java.util.List;

import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Database that stores information from the various closure RSS feeds
 */
public class FeedDatabase
{
    public static final long INVALIDROWID = -1; // Row ID that can never exist in the database 
    
    private static final String FEED_TABLE   = "feedtable";
    private static final String REGION_TABLE = "regiontable";
    
    public static final String COLUMN_ID = "_id";  // This is a well known column name in SQLite
    public static final String COLUMN_REGION = "state";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_MS = "datems";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_LAST_REFRESH = "lastrefresh";
    public static final String COLUMN_LATITUDE   = "geoLat";
    public static final String COLUMN_LONGTITUDE = "geoLong";
    
    // Raw SQL to create the database table
    private static final String CREATE_FEED_TABLE = 
        "CREATE TABLE "  + FEED_TABLE + "(" + 
        COLUMN_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_REGION + " INTEGER," +
        COLUMN_TITLE + " TEXT," + 
        COLUMN_DATE  + " TEXT," +
        COLUMN_DATE_MS  + " INTEGER," +
        COLUMN_LINK  + " TEXT," +
        COLUMN_GUID  + " TEXT," +
        COLUMN_DESCRIPTION + " TEXT," +
        COLUMN_CATEGORY + " TEXT," + 
        COLUMN_LATITUDE + " TEXT," + 
        COLUMN_LONGTITUDE + " TEXT);";
    
    // Raw SQL to create the region information table
    private static final String CREATE_REGION_TABLE =
        "CREATE TABLE " + REGION_TABLE + "(" + 
        COLUMN_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_REGION + " INTEGER UNIQUE," +    // Corresponds to the region code in the feed table
        COLUMN_LAST_REFRESH + " INTEGER);";     // last time that the feed for this region was updated
    
    // Raw SQL to drop the database closure table
    private static final String DROP_FEED_TABLE    = "DROP TABLE IF EXISTS "+ FEED_TABLE;
    private static final String DROP_REGION_TABLE  = "DROP TABLE IF EXISTS "+ REGION_TABLE;
    
    /**
     * Creates the actual tables into the database
     * @param db
     */
    public static void createTables(SQLiteDatabase db)
    {
        db.execSQL(CREATE_FEED_TABLE);
        db.execSQL(CREATE_REGION_TABLE);
    }

    /**
     * Drops the actual tables
     */
    public static void dropTables(SQLiteDatabase db)
    {
        db.execSQL(DROP_FEED_TABLE);
        db.execSQL(DROP_REGION_TABLE);
    }

    public static Cursor getFeedItemsForState(SQLiteDatabase db,int state)
    {
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_REGION).append(" = ?");
        Cursor c = db.query(FEED_TABLE, null, sqlWhereClause.toString(), new String[]{Integer.toString(state)}, null, null,null,null);
        return c;
    }
    
    /**
     * Deletes all of the rows from the database that match the given state
     * @param db
     * @param state
     * @return
     */
    public static int eraseAllEntriesForState(SQLiteDatabase db, int state)
    {
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_REGION).append(" = ?");
        int rowsDeleted = db.delete(FEED_TABLE, sqlWhereClause.toString(), new String[]{Integer.toString(state)});
        return rowsDeleted;
    }

    /**
     * Writes the given feed items to the database - this function should be called in the context of a transaction
     * @param db
     * @param feedItems
     * @param nsw
     */
    public static void writeFeedItemsToDatabase(SQLiteDatabase db,List<FeedItem> feedItems, int state)
    {
        for(FeedItem item : feedItems)
        {
            writeFeedItemToDatabase(db,item,state);
        }
    }

    /**
     * Updates the database using a transaction to delete existing entries for the given state and replace them with the new entries
     * @param db
     * @param feedItems
     * @param state
     */
    public static void updateDatabaseWithTransaction(SQLiteDatabase db,List<FeedItem> feedItems,int state)
    {
        try
        {
            db.beginTransaction();
            eraseAllEntriesForState(db,state);
            writeFeedItemsToDatabase(db, feedItems, state);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
    
    /**
     * Writes a single feed item to the database
     * @param db
     * @param item
     * @param state
     */
    private static void writeFeedItemToDatabase(SQLiteDatabase db,FeedItem item, int state)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_REGION, state);
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_LINK,item.getLink());
        values.put(COLUMN_GUID, item.getGuid());
        values.put(COLUMN_CATEGORY,item.getCategory());
        values.put(COLUMN_DATE_MS,CalendarUtils.createCalendarFromDateAndFormat(item.getDate(), CalendarUtils.getDateFormatForState(state)).getTimeInMillis());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_LATITUDE, item.getLatitude());
        values.put(COLUMN_LONGTITUDE, item.getLongtitude());
        db.insert(FEED_TABLE, null, values);
    }
    
    /**
     * Returns the list of items for a given state in date decreasing order (i.e the most recent event is first) 
     * @param db
     * @param state
     * @return
     */
    public static Cursor getItemsForStateSortedByDate(SQLiteDatabase db,int region)
    {
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_REGION).append(" = ?");
        StringBuilder orderByClause  = new StringBuilder(COLUMN_DATE_MS).append(" DESC ");
        return db.query(FEED_TABLE, null, sqlWhereClause.toString(), new String[]{Integer.toString(region)}, null, null, orderByClause.toString());
    }

    /**
     * Returns a list of items that correspond to the entries in the cursor
     * @param c
     * @return 
     */
    public static List<FeedItem> getItemsForCursor(final Cursor c)
    {
        ArrayList<FeedItem> items = new ArrayList<FeedItem>(c.getCount());
        if(c.moveToFirst())
        {
            int rowIdOffset= c.getColumnIndex(COLUMN_ID);
            int dateOffset = c.getColumnIndex(COLUMN_DATE);
            int guidOffset = c.getColumnIndex(COLUMN_GUID);
            int categoryOffset = c.getColumnIndex(COLUMN_CATEGORY);
            int linkOffset = c.getColumnIndex(COLUMN_LINK);
            int descriptionOffset = c.getColumnIndex(COLUMN_DESCRIPTION);
            int titleIndex = c.getColumnIndex(COLUMN_TITLE);
            int latOffset  = c.getColumnIndex(COLUMN_LATITUDE);
            int longOffset = c.getColumnIndex(COLUMN_LONGTITUDE);
            while(!c.isAfterLast())
            {
                String date = c.getString(dateOffset);
                String guid = c.getString(guidOffset);
                String title = c.getString(titleIndex);
                String category = c.getString(categoryOffset);
                String link = c.getString(linkOffset);
                String latitude   = c.getString(latOffset);
                String longtitude = c.getString(longOffset);
                String description = c.getString(descriptionOffset);
                int rowId = c.getInt(rowIdOffset);
                FeedItem item = new FeedItem(date, title, link, guid, description, FeedItem.getCategoriesFromString(category), latitude,longtitude,rowId);
                items.add(item);
                c.moveToNext();
            }
        }
        return items;
    }
    
    private static String getStringEntry(SQLiteDatabase db,long rowId,String column)
    {
        String data = null;
        StringBuilder sqlWhereStatement = new StringBuilder(COLUMN_ID).append(" = ?");
        Cursor c = db.query(FEED_TABLE, new String [] {column}, sqlWhereStatement.toString(), new String [] {Long.toString(rowId)}, null, null, null);
        if(c.moveToFirst())
        {
            data = c.getString(c.getColumnIndex(column));
        }
        c.close();
        return data;
    }
    
    /**
     * Returns the description for the given row id
     * @param rowId
     * @return
     */
    public static String getDescriptionForEntry(SQLiteDatabase db, long rowId)
    {
        return getStringEntry(db, rowId, COLUMN_DESCRIPTION);
    }

    /**
     * Returns the title for the given entry
     * @param db
     * @param dbRowId
     * @return
     */
    public static String getTitleForEntry(SQLiteDatabase db,long rowId)
    {
        return getStringEntry(db, rowId, COLUMN_TITLE);
    }

    public static String getLatForEntry(SQLiteDatabase db,long rowId)
    {
        return getStringEntry(db, rowId, COLUMN_LATITUDE);
    }
    
    public static String getLongForEntry(SQLiteDatabase db,long rowId)
    {
        return getStringEntry(db, rowId, COLUMN_LONGTITUDE);
    }
    
    /**
     * Returns the link for the given item
     * @param db
     * @param dbRowId
     * @return
     */
    public static String getLinkForEntry(SQLiteDatabase db, long dbRowId)
    {
        return getStringEntry(db, dbRowId, COLUMN_LINK);
    }
    
    /**
     * Returns the last update time for the given region
     * @param db
     * @param region
     * @return
     */
    public static long getLastUpdateTimeForRegion(SQLiteDatabase db, int region)
    {
        long lastUpdateTime = 0;
        StringBuilder query = new StringBuilder(COLUMN_REGION).append(" = ?");
        Cursor c = db.query(REGION_TABLE, new String [] {COLUMN_LAST_REFRESH}, query.toString(), new String [] {Integer.toString(region)}, null, null, null);
        if(c.moveToFirst())
        {
            lastUpdateTime = c.getLong(c.getColumnIndex(COLUMN_LAST_REFRESH));
        }
        c.close();
        return lastUpdateTime;
    }

    /**
     * Updates the entry in the database for the corresponding region with the time specified
     * @param mDb
     * @param mRegion
     * @param currentTimeMillis
     */
    public static void setLastUpdateTimeForRegion(SQLiteDatabase db, int region, long time)
    {
        ContentValues values = new ContentValues(2);
        values.put(COLUMN_REGION, region);
        values.put(COLUMN_LAST_REFRESH, time);
        db.insertWithOnConflict(REGION_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Returns a cursor that corresponds to the search target for the given region
     * @param mDb
     * @param mRegion
     * @param searchString
     * @return
     */
    public static Cursor searchForMatches(SQLiteDatabase db, int region,String searchString)
    {
        Cursor cursor;
        if(TextUtils.isEmpty(searchString))
        {
            cursor = getItemsForStateSortedByDate(db,region);
        }
        else
        {
            StringBuilder query = new StringBuilder(COLUMN_REGION).append(" = ? ").append(" and ").append(COLUMN_TITLE).append(" like '%").append(searchString).append("%'");
            StringBuilder orderByClause  = new StringBuilder(COLUMN_DATE_MS).append(" DESC ");
            cursor = db.query(FEED_TABLE, null, query.toString(), new String[]{Integer.toString(region)}, null, null,orderByClause.toString());
        }
        return cursor;
    }
    
    public static Cursor getCursorForRowId(SQLiteDatabase db, long rowid)
    {
        StringBuilder query = new StringBuilder(COLUMN_ID).append(" = ?");
        Cursor c = db.query(FEED_TABLE,null,query.toString(),new String[]{Long.toString(rowid)},null,null,null);
        return c;
    }
}
