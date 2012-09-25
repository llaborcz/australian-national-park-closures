package com.bluebottlesoftware.nationalparkclosures.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Database that stores information from the various closure RSS feeds
 */
public class ClosureDatabase 
{
    private static final String CLOSURE_TABLE_NAME = "closuretable";
    private static final String COLUMN_ID = "_id";  // This is a well known column name in SQLite
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_GUID = "guid";
    private static final String COLUMN_CATEGORY = "category";

    // Emnumeration of states
    public enum State
    {
        NSW,
        QLD,
        VIC,
        TAS,
        WA,
        SA,
        NT
    }
    
    // Raw SQL to create the database table
    private static final String CREATE_CLOSURE_TABLE = 
        "CREATE TABLE "  + CLOSURE_TABLE_NAME + 
        COLUMN_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_STATE + " INTEGER," +
        COLUMN_TITLE + " TEXT," + 
        COLUMN_DATE  + " TEXT," + 
        COLUMN_LINK  + " TEXT," +
        COLUMN_GUID  + " TEXT," +
        COLUMN_CATEGORY + " TEXT);";
        
    // Raw SQL to drop the database closure table
    private static final String DROP_CLOSURE_TABLE = "DROP TABLE IF EXISTS "+CLOSURE_TABLE_NAME;
    
    /**
     * Creates the actual tables into the database
     * @param db
     */
    public static void createTables(SQLiteDatabase db)
    {
        db.execSQL(CREATE_CLOSURE_TABLE);
    }

    /**
     * Drops the actual tables
     */
    public static void dropTables(SQLiteDatabase db)
    {
        db.execSQL(DROP_CLOSURE_TABLE);
    }
}
