package com.bluebottlesoftware.nationalparkclosures.database;

import java.util.ArrayList;
import java.util.List;

import com.bluebottlesoftware.nationalparkclosures.data.State;
import com.bluebottlesoftware.nationalparkclosures.parsers.DateFormats;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database that stores information from the various closure RSS feeds
 */
public class FeedDatabase
{
    public static final long INVALIDROWID = -1; // Row ID that can never exist in the database 
    
    private static final String FEED_TABLE = "closuretable";
    private static final String COLUMN_ID = "_id";  // This is a well known column name in SQLite
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DATE_MS = "datems";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_GUID = "guid";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";

    // Raw SQL to create the database table
    private static final String CREATE_FEED_TABLE = 
        "CREATE TABLE "  + FEED_TABLE + 
        COLUMN_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_STATE + " INTEGER," +
        COLUMN_TITLE + " TEXT," + 
        COLUMN_DATE  + " TEXT," +
        COLUMN_DATE_MS  + " INTEGER," +
        COLUMN_LINK  + " TEXT," +
        COLUMN_GUID  + " TEXT," +
        COLUMN_DESCRIPTION + " TEXT," +
        COLUMN_CATEGORY + " TEXT);";
        
    // Raw SQL to drop the database closure table
    private static final String DROP_CLOSURE_TABLE = "DROP TABLE IF EXISTS "+ FEED_TABLE;
    
    /**
     * Creates the actual tables into the database
     * @param db
     */
    public static void createTables(SQLiteDatabase db)
    {
        db.execSQL(CREATE_FEED_TABLE);
    }

    /**
     * Drops the actual tables
     */
    public static void dropTables(SQLiteDatabase db)
    {
        db.execSQL(DROP_CLOSURE_TABLE);
    }

    public Cursor getFeedItemsForState(SQLiteDatabase db,State state)
    {
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_STATE).append(" = ?");
        Cursor c = db.query(FEED_TABLE, null, sqlWhereClause.toString(), new String[]{Integer.toString(state.ordinal())}, null, null,null,null);
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
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_STATE).append(" = ").append(state);
        int rowsDeleted = db.delete(FEED_TABLE, sqlWhereClause.toString(), null);
        return rowsDeleted;
    }

    /**
     * Writes the given feed items to the database - this function should be called in the context of a transaction
     * @param db
     * @param feedItems
     * @param nsw
     */
    public static void writeFeedItemsToDatabase(SQLiteDatabase db,List<FeedItem> feedItems, State state)
    {
        for(FeedItem item : feedItems)
        {
            writeFeedItemToDatabase(db,item,state);
        }
    }

    /**
     * Writes a single feed item to the database
     * @param db
     * @param item
     * @param state
     */
    private static void writeFeedItemToDatabase(SQLiteDatabase db,FeedItem item, State state)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATE, state.ordinal());
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_LINK,item.getLink());
        values.put(COLUMN_GUID, item.getGuid());
        values.put(COLUMN_CATEGORY,item.getCategory());
        values.put(COLUMN_DATE_MS,item.getDateAsms());
        db.insert(FEED_TABLE, null, values);
    }
    
    /**
     * Returns the list of items for a given state in date decreasing order (i.e the most recent event is first) 
     * @param db
     * @param state
     * @return
     */
    public static Cursor getItemsForStateSortedByDate(SQLiteDatabase db,State state)
    {
        StringBuilder sqlWhereClause = new StringBuilder(COLUMN_STATE).append(" = ?");
        StringBuilder orderByClause  = new StringBuilder(COLUMN_DATE_MS).append(" DESC ");
        return db.query(FEED_TABLE, null, sqlWhereClause.toString(), new String[]{Integer.toString(state.ordinal())}, null, null, orderByClause.toString());
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
            int stateOffset = c.getColumnIndex(COLUMN_STATE);
            int titleIndex  = c.getColumnIndex(COLUMN_TITLE);
            while(!c.isAfterLast())
            {
                String date = c.getString(dateOffset);
                String guid = c.getString(guidOffset);
                String title = c.getString(titleIndex);
                String category = c.getString(categoryOffset);
                String link = c.getString(linkOffset);
                String description = c.getString(descriptionOffset);
                String dateFormat = DateFormats.getDateFormatForState(State.valueOf(Integer.toString(c.getInt(stateOffset))));
                int rowId = c.getInt(rowIdOffset);
                FeedItem item = new FeedItem(date, dateFormat, title, link, guid, description, category, rowId);
                items.add(item);
                c.moveToNext();
            }
        }
        return items;
    }
}
