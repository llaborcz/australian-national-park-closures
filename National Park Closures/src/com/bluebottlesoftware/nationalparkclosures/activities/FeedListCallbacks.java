package com.bluebottlesoftware.nationalparkclosures.activities;

public interface FeedListCallbacks
{
    /**
     * Called when the refresh starts. Also called when the activity is recreated and attached to the fragment
     */
    public void onRefreshStarted();
    
    /**
     * Called when the refresh finishes
     */
    public void onRefreshFinished();
    
    /**
     * Called when a list entry selected in the feed
     * @param rowId Database row id of the selected row
     */
    public void onListEntrySelected(long rowId);
}