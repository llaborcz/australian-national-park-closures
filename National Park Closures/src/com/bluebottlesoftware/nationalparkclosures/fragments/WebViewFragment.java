package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Fragment that contains the webview that shows the content associated with the feed.
 * When the loadFeedItem method is called the webview is upated with the content corresponding to that feed item
 */
public class WebViewFragment extends Fragment 
{
    public static final String KEY_DBROWID = "key_dbrowid"; /**<Key for the DB row ID that needs to be shown*/
    public static final String KEY_REGION  = "key_region";  /**<Region that content corresponds to*/  

    public static WebViewFragment newInstance(long dbRowId)
    {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_DBROWID, dbRowId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        // If we have active content we want to add in the "View in browser" menu item
        if(getArguments().getLong(KEY_DBROWID) != 0)
        {
            inflater.inflate(R.menu.webviewmenu, menu);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        boolean bResult = false;
        if(item.getItemId() == R.id.menu_viewInBrowser)
        {
            // Create an intent with the "link" of the selected item here
            long dbRowId = getArguments().getLong(KEY_DBROWID);
            if(dbRowId != 0)
            {
                DatabaseHelper helper = new DatabaseHelper(getActivity());
                SQLiteDatabase db = helper.getReadableDatabase();
                String link = FeedDatabase.getLinkForItem(db,dbRowId);
                if(link != null)
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
                db.close();
            }
        }
        return bResult;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.webviewfragmentlayout, group, false);
        WebView webView  = (WebView) v.findViewById(R.id.webview);
        long dbRowId = getArguments().getLong(KEY_DBROWID);
        if(0 != dbRowId)
        {
            int region = getArguments().getInt(KEY_REGION);
            // We've been told to load a description from the database
            DatabaseHelper helper = new DatabaseHelper(getActivity());
            SQLiteDatabase db   = helper.getReadableDatabase();
            String description  = FeedDatabase.getDescriptionForEntry(db, dbRowId);
            db.close();
            webView.loadDataWithBaseURL(Region.getBaseUrlForRegion(region), description, "text/html", "utf-8", "");
        }
        return v;
    }
}
