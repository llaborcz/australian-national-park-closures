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

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
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
 */
public class FeedListFragment extends ListFragment
{
    private static final String CURRENT_CHOICE_KEY = "currentchoice";
    private static final String CURRENT_ROWID_KEY  = "currentrowid";
    
    private RefreshFeedAsyncTask mRefreshFeedTask;
    private SQLiteDatabase       mDb;
    private int                  mRegion = Region.Nsw;
    private MenuItem             mRefreshMenuItem;
    private long                 mCurrentSelectedRowId = 0;
    private int                  mCurrentSelectedIndex = 0;

    /**
     * Creates an instance with the given region
     * @param region
     */
    public static FeedListFragment createInstance(int region)
    {
        FeedListFragment listFragment = new  FeedListFragment();
        listFragment.mRegion = region;
        return listFragment;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDb = new DatabaseHelper(getActivity()).getReadableDatabase();
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_CHOICE_KEY, mCurrentSelectedIndex);
        outState.putLong(CURRENT_ROWID_KEY, mCurrentSelectedRowId);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        
        // If this is the first time that we've been loaded and we've never refreshed we need to display an "Updating..." message
        long lastUpdate = FeedDatabase.getLastUpdateTimeForRegion(mDb, mRegion);
        if(0 == lastUpdate)
        {
            refreshFeed();
        }
        else
        {
            setEmptyText(getResources().getString(R.string.noEventsFound));
        }
        
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
        setListAdapter(new FeedDataAdapter(getActivity(), c, 0));

        if(savedInstanceState != null)
        {
            mCurrentSelectedIndex = savedInstanceState.getInt(CURRENT_CHOICE_KEY);
            mCurrentSelectedRowId = savedInstanceState.getLong(CURRENT_ROWID_KEY);
        }
        
        if(isDualPaneView())
        {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            if(mCurrentSelectedRowId != 0)
            {
                showDetails(getListView(),mCurrentSelectedIndex, mCurrentSelectedRowId);
            }
        }
    }

    
    /**
     * Returns flag indicating if this is dual pane mode or single pane mode
     * @return
     */
    private boolean isDualPaneView()
    {
        boolean bDualPane = false;
        View webViewLayout = getActivity().findViewById(R.id.webFragmentContent);
        if(webViewLayout != null && webViewLayout.getVisibility() == View.VISIBLE)
        {
            bDualPane = true;
        }
        return bDualPane;
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
        mRefreshMenuItem = menu.findItem(R.id.menu_refresh);
        if(mRefreshFeedTask != null)
        {
            // We're refreshing so we want to display the busy wait progress
            mRefreshMenuItem.setActionView(R.layout.refresh_menuitem_busy);
        }
    }
    
    /**
     * Called to refresh the feed - will keep content intact and will callback to parent
     * Changes the empty text to read Loading %s updates...
     */
    public void refreshFeed()
    {
        if(mRefreshFeedTask == null) 
        {
            mRefreshFeedTask = new RefreshFeedAsyncTask();
            mRefreshFeedTask.execute();
            String loadingText = getResources().getString(R.string.loading);
            setEmptyText(String.format(loadingText, getResources().getString(Region.getAsStringId(mRegion))));
            if(mRefreshMenuItem != null)
            {
                mRefreshMenuItem.setActionView(R.layout.refresh_menuitem_busy);
            }
        }
    }
    
    /**
     * Called when the user clicks an entry in the list
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        showDetails(listView, position,id);
    }
    
    /**
     * Shows the details for the selected feed item. In the case where we've got a two pane layout
     * this method will show the details in the webview fragment or start a new activity containing the webview fragment
     * @param position
     * @param id
     */
    private void showDetails(ListView listView, int position, long id)
    {
        mCurrentSelectedIndex = position;
        mCurrentSelectedRowId = id;
        if(isDualPaneView())
        {
            listView.setItemChecked(position, true);
            WebViewFragment detailsFragment = (WebViewFragment) getFragmentManager().findFragmentById(R.id.webFragmentContent);
            if(detailsFragment != null && detailsFragment.getShownId() != id)
            {
                // Create a new details fragment
                detailsFragment = WebViewFragment.newInstance(id);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webFragmentContent, detailsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(),com.bluebottlesoftware.nationalparkclosures.activities.DetailsViewActivity.class);
            intent.putExtra(WebViewFragment.KEY_DBROWID, id);
            intent.putExtra(WebViewFragment.KEY_REGION, mRegion);
            startActivity(intent);
        }
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
            mRefreshMenuItem.setActionView(null);
            if(result)
            {
                // Need to udpate the database table with the last update time for this region
                FeedDatabase.setLastUpdateTimeForRegion(mDb,mRegion,System.currentTimeMillis());
            }
            else
            {
                showRefreshError();
            }
            setEmptyText(getResources().getString(R.string.noEventsFound));
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
