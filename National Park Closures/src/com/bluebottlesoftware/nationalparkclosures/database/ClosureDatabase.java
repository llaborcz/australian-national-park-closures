package com.bluebottlesoftware.nationalparkclosures.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database that stores information from the various closure RSS feeds
 */
public class ClosureDatabase 
{
    SQLiteDatabase m_db;    /**<Backing database*/
    
    /**
     * Open helper for closure database 
     * @author lee
     *
     */
    private class ClosureDatabaseHelper extends SQLiteOpenHelper
    {

        public ClosureDatabaseHelper(Context context, String name,CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            
        }
        
    }
}
