package com.bluebottlesoftware.nationalparkclosures.activities;

import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.parkclosures.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;

public class ShowMapActivity extends Activity
{
    public static final String EXTRA_IDS = "ids";       // array of row ids that we need to display
    public static final String EXTRA_TITLE = "title";   // String to use as title
    private MapFragment mMapFragment;
    private long [] mRowIds;
    
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
        mRowIds = intent.getLongArrayExtra(EXTRA_IDS);       
        FragmentManager fm = getFragmentManager();
        mMapFragment = (MapFragment) fm.findFragmentById(R.id.mapFragmentContent);  
        if (mMapFragment == null) 
        {
            GoogleMapOptions options = new GoogleMapOptions();
            options.zoomControlsEnabled(false);
            options.tiltGesturesEnabled(true);
            mMapFragment = MapFragment.newInstance(options);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContent, mMapFragment);
            ft.commit();  
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        drawMapMarkers(mMapFragment,mRowIds);        
    }
    
    private void drawMapMarkers(MapFragment mapFragment, long [] rowIds)
    {
        Cursor c = FeedDatabase.getCursorForRowIds(DatabaseHelper.getDatabaseInstance(this), rowIds);
        if(c.moveToFirst())
        {
            
            GoogleMap map = mapFragment.getMap();
            if(null != map)
            {
                int titleColumnIndex = c.getColumnIndex(FeedDatabase.COLUMN_TITLE);
                int latColumnIndex   = c.getColumnIndex(FeedDatabase.COLUMN_LATITUDE);
                int longColumnIndex  = c.getColumnIndex(FeedDatabase.COLUMN_LONGTITUDE);
                LatLng latLng = null;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                while(!c.isAfterLast())
                {
                    String title = c.getString(titleColumnIndex);
                    String lat   = c.getString(latColumnIndex);
                    String lng   = c.getString(longColumnIndex);
                    latLng = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
                    builder.include(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(title);
                    markerOptions.position(latLng);                    
                    map.addMarker(markerOptions);
                    c.moveToNext();
                }
                
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),size.x,size.y,100);
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 8);
                map.moveCamera(cameraUpdate);
                
            }
        }
        c.close();
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
}
