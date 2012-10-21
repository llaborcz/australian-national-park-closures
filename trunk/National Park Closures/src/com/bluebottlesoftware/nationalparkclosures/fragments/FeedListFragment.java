package com.bluebottlesoftware.nationalparkclosures.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Fragment class that hosts the ListView that contains the feed.
 * This is retained between activity destroy / create events
 * TODO Set long click listener
 */
public class FeedListFragment extends ListFragment
{
    private RefreshFeedAsyncTask mRefreshFeedTask;
    private SQLiteDatabase       mDb;
    private int                  mRegion = Region.Nsw;
    private MenuItem             mRefresh;
    
    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDb = new DatabaseHelper(getActivity()).getReadableDatabase();
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.emptyText));
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
        setListAdapter(new FeedDataAdapter(getActivity(), c, 0));   
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        boolean bResult = false;
        switch(item.getItemId())
        {
        case R.id.menu_refresh:
            refreshFeed();
            bResult = true;
            break;
        }
        return bResult;
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        mRefresh = menu.findItem(R.id.menu_refresh);
        if(mRefreshFeedTask != null)
        {
            // We're refreshing so we want to display the busy wait progress
            mRefresh.setActionView(R.layout.refresh_menuitem_busy);
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
            if(mRefresh != null)
            {
                mRefresh.setActionView(R.layout.refresh_menuitem_busy);
            }
        }
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
        // TODO This is where we attach our selection listener
    }
    
    public class RefreshFeedAsyncTask extends AsyncTask<Integer, Void,Boolean>
    {
        final String TAG = "RefreshFeedAsyncTask";
        @Override
        protected Boolean doInBackground(Integer... params)
        {
            boolean bResult = false;
            try
            {
                FeedReader reader     = FeedReader.createInstance(mRegion);
                List <FeedItem> items = reader.connectAndGetFeedItems();
                FeedDatabase.updateDatabaseWithTransaction(mDb, items, mRegion);
                bResult = true;
            } 
            catch (MalformedURLException e)
            {
                Log.e(TAG,e.getMessage());
            } 
            catch (IllegalArgumentException e)
            {
                Log.e(TAG,e.getMessage());
            } 
            catch (XPathExpressionException e)
            {
                Log.e(TAG,e.getMessage());
            } 
            catch (SAXException e)
            {
                Log.e(TAG,e.getMessage());
            } 
            catch (IOException e)
            {
                Log.e(TAG,e.getMessage());
            } 
            catch (ParserConfigurationException e)
            {
                Log.e(TAG,e.getMessage());
            }
            return bResult;
        }
        
        @Override
        protected void onPostExecute(Boolean result)
        {
            mRefreshFeedTask = null;
            Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
            setListAdapter(new FeedDataAdapter(getActivity(), c, 0));
            mRefresh.setActionView(null);
            
            if(!result)
            {
                showRefreshError();
            }
        }
    }

    /**
     * Shows the refresh toast error
     */
    private void showRefreshError()
    {
        Toast toast = Toast.makeText(getActivity(), R.string.refreshError, Toast.LENGTH_SHORT);
        toast.show();
    }
}
