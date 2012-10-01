package com.bluebottlesoftware.nationalparkclosures.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DatabaseVersion = 1;
    private static final String DatabaseName = "closuredb.db";
    
    private static final String TAG = "DatabaseHelper";
    
    /**
     * Sets up the database backing file with this
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context,DatabaseName,null,DatabaseVersion);
    }

    /**
     * Called when the database first created
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG,"onCreate");
        FeedDatabase.createTables(db);
    }

    /**
     * Handles upgrades from previous versions - not needed now
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG,"onCreate");
        FeedDatabase.dropTables(db);
        onCreate(db);
    }
    
    public void dropAllTables(SQLiteDatabase db)
    {
        FeedDatabase.dropTables(db);
    }
}
