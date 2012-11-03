package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nswnpclosures.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
      MapView mapView =new MapView(getActivity(),MapViewDebugApiKey);
      mapView.setClickable(true);
      mapView.setSatellite(true);
      MapController mapController = mapView.getController();
      mapController.setCenter(mGeoPoint);
      mapController.setZoom(8);
      mapView.setBuiltInZoomControls(false);
      
      MapOverlay overlay = new MapOverlay(getResources().getDrawable(R.drawable.map_overlay));
      overlay.addItem(mGeoPoint, mTitle, mSnippet);
      mapView.getOverlays().add(overlay);
      ((ViewGroup)getView()).addView(mapView);
      mapView.invalidate();
    }
}
