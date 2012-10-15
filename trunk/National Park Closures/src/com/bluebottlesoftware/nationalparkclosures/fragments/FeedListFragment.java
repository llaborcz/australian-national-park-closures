package com.bluebottlesoftware.nationalparkclosures.fragments;

import com.bluebottlesoftware.nationalparkclosures.activities.FeedListCallbacks;
import com.bluebottlesoftware.nationalparkclosures.data.FeedDataAdapter;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Fragment class that hosts the ListView that contains the feed.
 * This is retained between activity destroy / create events
 * TODO Load the data
 * TODO Set long click listener
 */
public class FeedListFragment extends ListFragment
{
    private RefreshFeedAsyncTask mRefreshFeedTask;
    private SQLiteDatabase       mDb;
    private FeedListCallbacks    mCallbacks;
    
    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDb = new DatabaseHelper(getActivity()).getReadableDatabase(); 
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.emptyText));
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, Region.Nsw);
        setListAdapter(new FeedDataAdapter(getActivity(), c, 0));   
        if(mRefreshFeedTask != null)
        {
            mCallbacks.onRefreshStarted();
        }
    }

    /**
     * Called to refresh the feed - will keep content intact and will callback to parent
     */
    public void refreshFeed()
    {
        if(mRefreshFeedTask == null) 
        {
            mRefreshFeedTask = new RefreshFeedAsyncTask();
            mRefreshFeedTask.execute();
            mCallbacks.onRefreshStarted();
        }
    }
    
    /**
     * Sets the callbacks
     * @param callbacks
     */
    public void setActivityCallbacks(FeedListCallbacks callbacks)
    {
        mCallbacks = callbacks;
    }
    
    /**
     * Called when the user clicks an entry in the list
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        // TODO Find the entry and start the webview activity
    }
    
    public class RefreshFeedAsyncTask extends AsyncTask<Integer, Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... params)
        {
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
        
        @Override
        protected void onPostExecute(Boolean result)
        {
            mRefreshFeedTask = null;
            mCallbacks.onRefreshFinished();
            Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, Region.Nsw);
            setListAdapter(new FeedDataAdapter(getActivity(), c, 0));
        }
    }
}
