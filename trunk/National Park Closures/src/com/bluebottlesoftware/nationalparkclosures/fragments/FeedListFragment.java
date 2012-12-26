package com.bluebottlesoftware.nationalparkclosures.fragments;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.bluebottlesoftware.nationalparkclosures.activities.DetailsViewActivity;
import com.bluebottlesoftware.nationalparkclosures.activities.ShowMapActivity;
import com.bluebottlesoftware.nationalparkclosures.data.FeedDataAdapter;
import com.bluebottlesoftware.nationalparkclosures.data.FeedReader;
import com.bluebottlesoftware.nationalparkclosures.data.Region;
import com.bluebottlesoftware.nationalparkclosures.database.DatabaseHelper;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.parkclosures.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
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
     * This handles our search callbacks
     */
    private final SearchView.OnQueryTextListener mQueryListeneter= new SearchView.OnQueryTextListener()
    {
        /**
         * Called when the search is submitted - does nothing since we just rely on the text change callback
         */
        @Override
        public boolean onQueryTextSubmit(String query)
        {
            return false;
        }
        
        /**
         * Called when the search text changes - 
         */
        @Override
        public boolean onQueryTextChange(String searchString)
        {
            if(!TextUtils.isEmpty(searchString))
            {
                setEmptyText(getString(R.string.noEventsFound));    // Handles the case where refresh has the alternate empty text
            }
            updateListViewForSearch(searchString);
            return true;
        }
    };

    /**
     * This class is needed because on ICS the SearchView.onCloseListener is not invoked
     */
    private final MenuItem.OnActionExpandListener mSearchOpenCloseListener = new MenuItem.OnActionExpandListener()
    {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item)
        {
            return true;
        }
        
        @Override
        public boolean onMenuItemActionCollapse(MenuItem item)
        {
            updateListViewForSearch(null);
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQuery("", false);
            searchView.setIconified(true);
            return true;
        }
    };
    private FeedDataAdapter mAdapter;

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
        mDb = DatabaseHelper.getDatabaseInstance(getActivity());
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

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
        Activity activity = getActivity();
        if(activity != null)
        {
            mAdapter = new FeedDataAdapter(getActivity(), c, 0);
            setListAdapter(mAdapter);
            if(mRefreshFeedTask!=null)
            {
                setEmptyText(getResources().getString(R.string.noEventsFound));
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

    /**
     * Registers the search callback to the searchview
     */
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.activity_main, menu);
        MenuItem searchMenu = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView)searchMenu.getActionView();
        searchView.setOnQueryTextListener(mQueryListeneter);
        searchMenu.setOnActionExpandListener(mSearchOpenCloseListener);
        
        SQLiteDatabase db = DatabaseHelper.getDatabaseInstance(getActivity());
        if(!FeedDatabase.hasRegionAnyGeoEvents(db,mRegion))
        {
            menu.removeItem(R.id.menu_mapregion);
        }
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
            
        case R.id.menu_mapregion:
            long [] dbRowIds = FeedDatabase.getRowIdsForGeoEventsInRegions(mDb,new int [] {mRegion});
            Intent mapIntent = new Intent();
            mapIntent.setClass(getActivity(), ShowMapActivity.class);
            mapIntent.putExtra(ShowMapActivity.EXTRA_IDS, dbRowIds);
            mapIntent.putExtra(ShowMapActivity.EXTRA_TITLE, getString(Region.getAsStringId(mRegion)));
            startActivity(mapIntent);
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
            setEmptyText(getResources().getString(R.string.loading));
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
                detailsFragment = WebViewFragment.newInstance(id,mRegion);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webFragmentContent, detailsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(),DetailsViewActivity.class);
            intent.putExtra(WebViewFragment.KEY_DBROWID, id);
            intent.putExtra(WebViewFragment.KEY_REGION, mRegion);
            startActivity(intent);
        }
    }

    public class RefreshFeedAsyncTask extends AsyncTask<Integer, Void,Boolean>
    {
        final static String TAG = "RefreshFeedAsyncTask";
        @Override
        protected Boolean doInBackground(Integer... params)
        {
            boolean bResult = false;
            try
            {                
                FeedReader reader     = FeedReader.createInstance(mRegion);
                reader.writeFeedItemsToDatabase(mDb, mRegion);
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
            mRefreshMenuItem.setActionView(null);
            if(result)
            {
                getActivity().invalidateOptionsMenu();
                if(mAdapter != null)
                {
                    Cursor c = FeedDatabase.getItemsForStateSortedByDate(mDb, mRegion);
                    Activity activity = getActivity();
                    if(activity != null)
                    {
                        mAdapter = new FeedDataAdapter(getActivity(), c, 0);
                        setListAdapter(mAdapter);
                    }                    
                }
                // Need to udpate the database table with the last update time for this region but only if the region is still the same
                // as it was when we started the search
                FeedDatabase.setLastUpdateTimeForRegion(mDb,mRegion,System.currentTimeMillis());
            }
            else
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
        Toast.makeText(getActivity(), R.string.refreshError, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Single point where the listview gets updated. If search != mCurrentSearch only then does the view get updated
     * @param search
     */
    private void updateListViewForSearch(String search)
    {
        Cursor cursor = FeedDatabase.searchForMatches(mDb,mRegion,search);
        setListAdapter(new FeedDataAdapter(getActivity(),cursor, 0));
    }
}
