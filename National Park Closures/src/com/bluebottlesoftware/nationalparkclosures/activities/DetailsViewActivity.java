package com.bluebottlesoftware.nationalparkclosures.activities;

import android.os.Debug;
import android.view.View;
import android.widget.TextView;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.fragments.WebViewFragment;
import com.bluebottlesoftware.parkclosures.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Activity that shows the webview fragment by itself
 * TODO Make the title of the entry being shown the title 
 */
public class DetailsViewActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        long dbRowId = getIntent().getLongExtra(WebViewFragment.KEY_DBROWID,0);
        String title = null;
        if(0 != dbRowId)
        {
            SQLiteDatabase db = DatabaseHelper.getDatabaseInstance(this);
            title = FeedDatabase.getTitleForEntry(db,dbRowId);
            setTitle(title);
        }
        setContentView(R.layout.webviewactivitylayout);
        final FragmentManager fm = getFragmentManager();
        WebViewFragment webViewFragment = (WebViewFragment) fm.findFragmentById(R.id.webviewFragmentContent);
        if(webViewFragment == null)
        {
            int region = getIntent().getIntExtra(WebViewFragment.KEY_REGION,Region.Nsw);
            webViewFragment = WebViewFragment.newInstance(dbRowId,region);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.webviewFragmentContent, webViewFragment);
            ft.commit();  
        }
        if(title != null)
        {
            TextView titleView = (TextView)findViewById(R.id.title);
            titleView.setText(title);
            titleView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    WebViewFragment webViewFragment = (WebViewFragment) fm.findFragmentById(R.id.webviewFragmentContent);
                    webViewFragment.showArticleInBrowser();
                }
            });
        }
    }
    
    /**
     * Handles the action bar home button
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        boolean bResult = false;
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            bResult = true;
        }
        return bResult;
    }
}
