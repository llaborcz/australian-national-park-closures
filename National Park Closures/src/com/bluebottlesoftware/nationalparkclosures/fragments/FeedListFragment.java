package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nswnpclosures.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Fragment class that hosts the ListView that contains the feed.
 * TODO Load the data
 * TODO Set long click listener
 */
public class FeedListFragment extends ListFragment
{
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.emptyText));
    }
    
    /**
     * Called when the user clicks an entry in the list
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        // TODO Find the entry and start the webview activity
    }
}
