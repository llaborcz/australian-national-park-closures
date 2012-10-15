package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

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

    /**
     * Saved state:
     * State being viewed
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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
    public void onRefreshStarted()
    {
        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onRefreshFinished()
    {
        setProgressBarIndeterminateVisibility(false);        
    }
}
