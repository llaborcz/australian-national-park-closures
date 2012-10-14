package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.data.FeedDataAdapter;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;

/**
 * This is the activity that displays the feed for a particular state. 
 * TOOD Add refresh action bar entry
 * TODO Change the icon
 * TODO Change the title to match the state that's being displayed
 */
public class FeedListActivity extends Activity
{
    private static final String CURRENTREGIONKEY = "currentregion"; // Key for the current region being viewed
    
    private int mRegion; // Region being viewed
    
    /**
     * Saved state:
     * State being viewed
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            // We've got a saved state
            mRegion = savedInstanceState.getInt(CURRENTREGIONKEY);
            setTitle(Region.getAsStringId(mRegion));
        }
        else
        {
            // Set the region to the default region
            mRegion = Region.Nsw;
            setTitle(R.string.nsw);
        }
        
        setContentView(R.layout.listviewactivity);
        FragmentManager fm = getFragmentManager();
        FeedListFragment listFragment = (FeedListFragment) fm.findFragmentById(R.id.listFragmentContent);  
        
        if (listFragment == null) 
        {
            listFragment = new FeedListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.listFragmentContent, listFragment);
            ft.commit();  
        }
        
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(db, Region.Nsw);
        listFragment.setListAdapter(new FeedDataAdapter(this, c, 0));
    }
    
    /**
     * Saves the current state: 
     * List being viewed
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(CURRENTREGIONKEY,mRegion);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
