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
import android.text.Html;
import android.text.TextUtils;
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

    private static final String MimeTypeHtml = "text/html";
    private static final String Utf8Encoding = "utf-8";
    
    private long mDbRowId;  // row id of content being displayed
    private int  mRegion;   // region
    private String mLat;    // geo information
    private String mLong;   // geo information
    
    public static WebViewFragment newInstance(long dbRowId,int region)
    {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_DBROWID, dbRowId);
        args.putInt(KEY_REGION, region);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDbRowId = getArguments().getLong(KEY_DBROWID);
        mRegion  = getArguments().getInt(KEY_REGION);
    }
    
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        if(mDbRowId != 0)
        {
            inflater.inflate(R.menu.webviewmenu, menu);
            // If we've got no geo information for the currently viewed event we hide the map button
            DatabaseHelper helper = new DatabaseHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            mLat = FeedDatabase.getLatForEntry(db, mDbRowId);
            mLong= FeedDatabase.getLongForEntry(db, mDbRowId);
            if(TextUtils.isEmpty(mLat) || TextUtils.isEmpty(mLong))
            {
                menu.removeItem(R.id.menu_showOnMap);
            }
            db.close();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        boolean bResult = false;
        long dbRowId = getArguments().getLong(KEY_DBROWID);
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        
        switch(item.getItemId())
        {
            case R.id.menu_viewInBrowser:
            {
                // Create an intent with the "link" of the selected item here
                if(dbRowId != 0)
                {
                    String link = FeedDatabase.getLinkForEntry(db,dbRowId);
                    if(link != null)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    }
                }
            }
            
            case R.id.menu_shareArticle:
                String description  = FeedDatabase.getDescriptionForEntry(db, getArguments().getLong(KEY_DBROWID));
                String title        = FeedDatabase.getTitleForEntry(db, dbRowId);
                Intent shareIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"));
                shareIntent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(description));
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubjectPreamble)+title);
                startActivity(shareIntent);
                break;
        }
        
        db.close();
        return bResult;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.webviewfragmentlayout, group, false);
        WebView webView  = (WebView) v.findViewById(R.id.webview);
        if(0 != mDbRowId)
        {
            // We've been told to load a description from the database
            DatabaseHelper helper = new DatabaseHelper(getActivity());
            SQLiteDatabase db   = helper.getWritableDatabase();
            String description  = FeedDatabase.getDescriptionForEntry(db, mDbRowId);
            db.close();
            String baseUrl = Region.getBaseUrlForRegion(mRegion);
            webView.loadDataWithBaseURL(baseUrl, description, MimeTypeHtml, Utf8Encoding, "");
        }
        return v;
    }

    public long getShownId()
    {
        return getArguments().getLong(KEY_DBROWID);
    }
}
