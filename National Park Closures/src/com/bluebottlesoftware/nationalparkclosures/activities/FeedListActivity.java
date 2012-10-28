package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nationalparkclosures.fragments.WebViewFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * This is the activity that displays the feed for a particular region. 
 */
public class FeedListActivity extends Activity
{
    private static final String CURRENTREGIONKEY = "currentregion"; // Key for the current region being viewed
    private int mRegion;                    // Region being viewed
    
    /**
     * Saved state:
     * Region being viewed
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null)
        {
            // Set the region to the default region
            mRegion = Region.Nsw;
            setTitle(R.string.nsw);
        }
        else
        {
            // We've got a saved state
            mRegion  = savedInstanceState.getInt(CURRENTREGIONKEY);
            setTitle(Region.getAsStringId(mRegion));
        }
        FragmentManager fm = getFragmentManager();
        FeedListFragment listFragment = (FeedListFragment) fm.findFragmentById(R.id.listFragmentContent);  
        if (listFragment == null) 
        {
            listFragment = FeedListFragment.createInstance(mRegion);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.listFragmentContent, listFragment);
            ft.commit();  
        }
        FrameLayout webViewFrame = (FrameLayout) findViewById(R.id.webFragmentContent);
        if(webViewFrame != null)
        {
            WebViewFragment webViewFragment = (WebViewFragment)fm.findFragmentById(R.id.webFragmentContent);
            if(webViewFragment == null)
            {
                webViewFragment = WebViewFragment.newInstance(0,0);
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.webFragmentContent, webViewFragment);
                ft.commit();
            }
        }
    }
    
    /**
     * Saves the current state: 
     * Region being viewed
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENTREGIONKEY,mRegion);
    }
}
