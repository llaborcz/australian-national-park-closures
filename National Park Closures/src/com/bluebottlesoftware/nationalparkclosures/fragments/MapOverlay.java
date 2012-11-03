package com.bluebottlesoftware.nationalparkclosures.fragments;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

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
