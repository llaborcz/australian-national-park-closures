package com.bluebottlesoftware.nationalparkclosures.activities;

import android.os.Debug;
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
import com.google.android.gms.maps.model.Marker;
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

import java.util.HashMap;

public class ShowMapActivity extends Activity
{
    public static final String EXTRA_IDS = "ids";       // array of row ids that we need to display
    public static final String EXTRA_TITLE = "title";   // String to use as title
    private MapFragment mMapFragment;
    private long [] mRowIds;
    private HashMap<String,Long> mMarkerMap;    // Map of marker ids to database row ids

    @Override 
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        String title  = intent.getStringExtra(EXTRA_TITLE);
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
        mMarkerMap = new HashMap<String, Long>();
        setupMap(mMapFragment,mRowIds);        
    }
    
    /**
     * Puts the map markers onto the map and sets up the callback
     * @param mapFragment
     * @param rowIds
     */
    private void setupMap(MapFragment mapFragment, long [] rowIds)
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
                int idColumnIndex    = c.getColumnIndex(FeedDatabase.COLUMN_ID);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng latLng = null;
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
                    Marker marker = map.addMarker(markerOptions);
                    mMarkerMap.put(marker.getId(),c.getLong(idColumnIndex));
                    c.moveToNext();
                }

                // The following code draws positions the camera so that all of the markers are in view
                // If we've only got a single map point then we want to make the visible area a lot larger
                Point size = new Point();
                CameraUpdate cameraUpdate = null;
                getWindowManager().getDefaultDisplay().getSize(size);
                if(rowIds.length == 1)
                {
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,(float)10.0);
                }
                else
                {
                    cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),size.x,size.y,100);
                }
                map.moveCamera(cameraUpdate);
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
                {
                    // This is the callback invoked when an information window is clicked
                    @Override
                    public void onInfoWindowClick(Marker marker)
                    {
                        Debug.waitForDebugger();
                        String id = marker.getId();
                        Long dbRowId = mMarkerMap.get(id);
                        if(null != dbRowId)
                        {
                            // We need to start the activity that shows the information for this marker
                        }
                    }
                });
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
