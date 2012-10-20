package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Fragment that contains the webview that shows the content associated with the feed.
 * When the loadFeedItem method is called the webview is upated with the content corresponding to that feed item
 */
public class WebViewFragment extends Fragment 
{
    private static final String KEY_DBROWID = "dbrowid";    /**<Key for the DB row ID that needs to be shown*/
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
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.webviewfragmentlayout, group, false);
        WebView webView  = (WebView) v.findViewById(R.id.webview);
        // TODO If we've been told to show an index we need to load it now from the database
        
        long dbRowId = getArguments().getLong(KEY_DBROWID);
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        // TODO Get the description string for the cursor
        return v;
    }
}
