package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This is the activity that displays the feed for a particular state. 
 * TOOD Add refresh action bar entry
 * TODO Change the icon
 * TODO Change the title to match the state that's being displayed
 */
public class FeedListActivity extends Activity implements FeedListCallbacks
{
    private static final String CURRENTREGIONKEY = "currentregion"; // Key for the current region being viewed
    private int mRegion;                    // Region being viewed
    private FeedListFragment mListFragment; // Our retained list fragment
    private MenuItem mRefresh;
    private boolean mbRefresh;
    
    /**
     * Saved state:
     * State being viewed
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewactivity);
        FragmentManager fm = getFragmentManager();
        mListFragment = (FeedListFragment) fm.findFragmentById(R.id.listFragmentContent);  
        if (mListFragment == null) 
        {
            mListFragment = new FeedListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.listFragmentContent, mListFragment);
            ft.commit();  
        }
        
        mListFragment.setActivityCallbacks(this);
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        boolean bResult = false;
        switch(item.getItemId())
        {
        case R.id.menu_refresh:
            mListFragment.refreshFeed();
            bResult = true;
            break;
        }
        return bResult;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        mRefresh = menu.findItem(R.id.menu_refresh);
        if(mbRefresh)
        {
            mRefresh.setActionView(R.layout.refresh_menuitem);
        }
        return true; 
    }
    
    @Override
    public void onRefreshStarted()
    {
        mbRefresh = true;
        if(mRefresh != null)
        {
            mRefresh.setActionView(R.layout.refresh_menuitem);
        }
    }

    @Override
    public void onRefreshFinished()
    {
        mbRefresh = false;
        mRefresh.setActionView(null);
    }

    @Override
    public void onListEntrySelected(long rowId)
    {
    }
}
