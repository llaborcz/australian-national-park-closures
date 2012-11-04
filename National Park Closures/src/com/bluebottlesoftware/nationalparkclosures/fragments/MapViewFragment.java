package com.bluebottlesoftware.nationalparkclosures.fragments;

import java.util.ArrayList;

import com.bluebottlesoftware.nswnpclosures.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Fragment that displays a MapView
 */
public class MapViewFragment extends Fragment
{
    private static final String MapViewDebugApiKey = "0TiX-2-6j0HyUn03vC91X89qny0PykFadoZIL0Q";
    private GeoPoint    mGeoPoint;
    private String      mTitle;
    private String      mSnippet;
    private MapView     mMapView;
    
    public class MapOverlay extends ItemizedOverlay<OverlayItem>
    {
        private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();
        
        public MapOverlay(Drawable overlayDrawable)
        {
            super(boundCenterBottom(overlayDrawable));
            populate();
        }

        public void addItem(GeoPoint point,String title,String snippet)
        {
            OverlayItem newItem = new OverlayItem(point, title, snippet);
            mItemList.add(newItem);
            populate();
        }
        
        @Override
        protected OverlayItem createItem(int index)
        {
            return mItemList.get(index);
        }

        @Override
        public int size()
        {
            return mItemList.size();
        }

    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
    /**
     * Creates an instance of this fragment with the appropriate center point
     * @return
     */
    public static MapViewFragment createInstance(float longtitude,float latitude,String title,String snippet)
    {   
        MapViewFragment fragment = new MapViewFragment();
        fragment.mGeoPoint = new GeoPoint((int)(latitude * 1E6), (int)(longtitude * 1E6));
        fragment.mSnippet  = snippet;
        fragment.mTitle    = title;
        return fragment;
    }
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
    {
        return(new FrameLayout(getActivity()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
      super.onActivityCreated(savedInstanceState);
      mMapView = new MapView(getActivity(),MapViewDebugApiKey);
      mMapView.setClickable(true);
      mMapView.setSatellite(false);
      MapController mapController = mMapView.getController();
      mapController.setCenter(mGeoPoint);
      mapController.setZoom(8);
      mMapView.setBuiltInZoomControls(false);
      
      MapOverlay overlay = new MapOverlay(getResources().getDrawable(R.drawable.map_overlay));
      overlay.addItem(mGeoPoint, mTitle, mSnippet);
      mMapView.getOverlays().add(overlay);
      ((ViewGroup)getView()).addView(mMapView);
      mMapView.invalidate();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mapviewmenu,menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean bResult = false;
        switch(item.getItemId())
        {
        case R.id.menu_toggleSatellite:
            bResult = true;
            mMapView.setSatellite(!item.isChecked());
            item.setChecked(!item.isChecked());
            break;
        }
        
        return bResult;
    }
}