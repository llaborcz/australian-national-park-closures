package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.fragments.FeedListFragment;
import com.bluebottlesoftware.nswnpclosures.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

/**
 * This is the activity that displays the feed for a particular state. 
 * TOOD Add refresh action bar entry
 * TODO Change the icon
 * TODO Change the title to match the state that's being displayed
 */
public class FeedListActivity extends Activity implements RefreshListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        FeedListFragment listFragment = (FeedListFragment) fm.findFragmentById(R.id.listFragmentContent);  
        
        if (listFragment == null) 
        {
            listFragment = new FeedListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.listFragmentContent, listFragment);
            ft.commit();  
        }
        listFragment.setRefreshRequestedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Callback from listfragment
     */
    @Override
    public void onRefreshRequestedFromFragment()
    {
        Log.d("FeedListActivity","Entry");
    }
}
