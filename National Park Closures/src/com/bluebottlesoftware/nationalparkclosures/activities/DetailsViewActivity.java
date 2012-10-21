package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.fragments.WebViewFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Activity that shows the webview fragment by itself
 * TODO Make the title of the entry being shown the title 
 */
public class DetailsViewActivity extends Activity
{
    private Fragment mWebFragment;  // Reference to our webview fragment

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        
        long dbRowId = getIntent().getLongExtra(WebViewFragment.KEY_DBROWID,0);
        if(0 != dbRowId)
        {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            setTitle(FeedDatabase.getTitleForEntry(db,dbRowId));
        }
        setContentView(R.layout.webviewactivitylayout);
        FragmentManager fm = getFragmentManager();
        mWebFragment = fm.findFragmentById(R.id.webviewFragmentContent);
        if(mWebFragment == null)
        {
            mWebFragment = WebViewFragment.newInstance(dbRowId);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.webviewFragmentContent, mWebFragment);
            ft.commit();  
        }
    }
}