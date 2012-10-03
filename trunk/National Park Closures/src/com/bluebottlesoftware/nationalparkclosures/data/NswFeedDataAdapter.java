package com.bluebottlesoftware.nationalparkclosures.data;

import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.nationalparkclosures.parsers.DateFormats;
import com.bluebottlesoftware.nswnpclosures.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Adapter which converts a dataset containing rows of NSW feed data into a visible representation of the data
 */
public class NswFeedDataAdapter extends CursorAdapter
{
    private LayoutInflater mLayoutInflater; // inflates our views
    private int mTitleColumn;       // Column index of title
    private int mDateColumn;        // Column index of date
    private int mCategoryColumn;    // Column index of category
    
    public NswFeedDataAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTitleColumn = c.getColumnIndex(FeedDatabase.COLUMN_TITLE);
        mDateColumn  = c.getColumnIndex(FeedDatabase.COLUMN_DATE);
        mCategoryColumn = c.getColumnIndex(FeedDatabase.COLUMN_CATEGORY);
    }

    /**
     * Binds the data at the current row in the cursor to the given view, if the given view is null then 
     * a new view is created
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        String title = context.getString(mTitleColumn);
        String date  = context.getString(mDateColumn);
        String category = context.getString(mCategoryColumn);
        
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView dateView  = (TextView) view.findViewById(R.id.date);
        TextView categoryView = (TextView) view.findViewById(R.id.category);
        
        titleView.setText(title);
        dateView.setText(DateFormats.getDisplayFriendlyDateForRegion(Region.Nsw,date));
        categoryView.setText(category);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rowView = mLayoutInflater.inflate(R.layout.nswrow, parent);
        bindView(rowView, context, cursor);
        return rowView;
    }
}
