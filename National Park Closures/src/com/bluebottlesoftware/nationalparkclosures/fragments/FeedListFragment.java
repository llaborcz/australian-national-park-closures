package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nationalparkclosures.activities.RefreshListener;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment class that hosts the ListView that contains the feed
 */

public class FeedListFragment extends ListFragment
{
    private RefreshListener mListener;
    
    public void setRefreshRequestedListener(RefreshListener listener)
    {
        mListener = listener;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
    }
    
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View listView = inflater.inflate(R.layout.nationalparkeventlayout,container,false);
        Button refreshButton = (Button) listView.findViewById(R.id.refreshButton);
        
        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onRefreshRequestedFromFragment();
            }
        });
        return listView;
    }
}
