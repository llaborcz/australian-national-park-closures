package com.bluebottlesoftware.nationalparkclosures.data;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Adapter which converts a dataset containing rows of NSW feed data into a visible representation of the data
 */
public class NswFeedDataAdapter extends CursorAdapter
{
    public NswFeedDataAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    /**
     * Binds the data at the current row in the cursor to the given view, if the given view is null then 
     * a new view is created
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
