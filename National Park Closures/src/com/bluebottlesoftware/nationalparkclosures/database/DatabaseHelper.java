package com.bluebottlesoftware.nationalparkclosures.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DatabaseVersion = 1;
    private static final String DatabaseName = "closuredb.db";
    
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
        ClosureDatabase.createTables(db);
    }

    /**
     * Handles upgrades from previous versions - not needed now
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        ClosureDatabase.dropTables(db);
        onCreate(db);
    }
}
