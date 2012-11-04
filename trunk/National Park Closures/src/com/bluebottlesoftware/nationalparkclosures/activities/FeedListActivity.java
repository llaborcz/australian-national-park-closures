package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nationalparkclosures.fragments.WebViewFragment;
import com.bluebottlesoftware.parkclosures.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.OnNavigationListener;

/**
 * This is the activity that displays the feed for a particular region. 
 */
public class FeedListActivity extends Activity
{
    private static final String CURRENTREGIONKEY = "currentregion"; // Key for the current region being viewed
    private int mRegion = Region.Nsw; // Region being viewed
    private SpinnerAdapter mAdapter;
    
    /**
     * Callback that is used when the user changes the region
     */
    private OnNavigationListener mNavigationListener = new OnNavigationListener()
    {        
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            boolean bNewFragment = false;   // Indicates if we're creating a new fragment or reusing an existing one
            FeedListFragment currentFragment = null;
            FeedListFragment fragment = (FeedListFragment) getFragmentManager().findFragmentByTag(Integer.toString(itemPosition));
            if(null == fragment)
            {
                // We don't have this fragment yet
                fragment = FeedListFragment.createInstance(itemPosition);
                bNewFragment = true;
            }
            
            currentFragment = (FeedListFragment) getFragmentManager().findFragmentById(R.id.listFragmentContent);
            
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if(currentFragment != null)
            {
                transaction.detach(currentFragment);
            }
            
            if(bNewFragment)
            {
                transaction.add(R.id.listFragmentContent, fragment,Integer.toString(itemPosition));
            }
            else
            {
                transaction.attach(fragment);
            }
            
            transaction.commit();
            mRegion = itemPosition;
            return true;
        }
    };
    
    /**
     * Saved state:
     * Region being viewed
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","Entry");
        if(savedInstanceState != null)
        {
            mRegion = savedInstanceState.getInt(CURRENTREGIONKEY);
        }
        mAdapter = ArrayAdapter.createFromResource(this, R.array.regions, android.R.layout.simple_spinner_dropdown_item);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mAdapter, mNavigationListener);
        actionBar.setTitle(null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_USE_LOGO);
        actionBar.setSelectedNavigationItem(mRegion);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
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
