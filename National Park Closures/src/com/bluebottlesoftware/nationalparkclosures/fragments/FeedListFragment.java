package com.bluebottlesoftware.nationalparkclosures.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.activities.FeedListCallbacks;
import com.bluebottlesoftware.nationalparkclosures.data.FeedDataAdapter;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.network.FeedReader;
import com.bluebottlesoftware.nationalparkclosures.parsers.FeedItem;
import com.bluebottlesoftware.nswnpclosures.R;

import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
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
    private int                  mRegion = Region.Nsw;
    
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
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
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
     * Sets the current region
     * @param region
     */
    public void setRegion(int region)
    {
        mRegion = region;
        // TODO Once we support additional regions here display them
    }
    
    /**
     * Called when the user clicks an entry in the list
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        mCallbacks.onListEntrySelected(id);
    }
    
    public class RefreshFeedAsyncTask extends AsyncTask<Integer, Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... params)
        {
            try
            {
                FeedReader reader     = FeedReader.createInstance(mRegion);
                List <FeedItem> items = reader.connectAndGetFeedItems();
                FeedDatabase.updateDatabaseWithTransaction(mDb, items, mRegion);
            } catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XPathExpressionException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParserConfigurationException e)
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
            Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
            setListAdapter(new FeedDataAdapter(getActivity(), c, 0));
        }
    }
}
