package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.fragments.MapViewFragment;
import com.bluebottlesoftware.parkclosures.R;
import com.google.android.maps.MapActivity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ShowMapActivity extends MapActivity
{
    public static final String EXTRA_IDS = "ids";       // array of row ids that we need to display
    public static final String EXTRA_TITLE = "title";   // String to use as title
    
    @Override 
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title  = intent.getStringExtra(EXTRA_TITLE);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        title = (title == null) ? getString(R.string.mapActivityTitle) : title;
        actionBar.setTitle(title);
        setContentView(R.layout.mapactivity);
        
        FragmentManager fm = getFragmentManager();
        MapViewFragment mapFragment = (MapViewFragment) fm.findFragmentById(R.id.mapFragmentContent);  
        if (mapFragment == null) 
        {
            mapFragment = MapViewFragment.createInstance(intent.getLongArrayExtra(EXTRA_IDS));
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContent, mapFragment);
            ft.commit();  
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
    
    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }
}
