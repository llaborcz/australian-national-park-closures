package com.bluebottlesoftware.nationalparkclosures.data;

import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
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
    private int mIdColumn;          // Column index of our _id row
    
    public NswFeedDataAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTitleColumn = c.getColumnIndex(FeedDatabase.COLUMN_TITLE);
        mDateColumn  = c.getColumnIndex(FeedDatabase.COLUMN_DATE);
        mCategoryColumn = c.getColumnIndex(FeedDatabase.COLUMN_CATEGORY);
        mIdColumn = c.getColumnIndex(FeedDatabase.COLUMN_ID);
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
        dateView.setText(getFormattedDateForNSW(date));
        categoryView.setText(category);
        view.setId((int) cursor.getLong(mIdColumn));    // This'll allow us to get back our database ID
        
        // Now set the onClick listener for the row which will cause us to start our web activity that contains
        // more information
        
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int entryRowId = v.getId();
                if(View.NO_ID != entryRowId)
                {
                    // TODO At this point we need to start our webview activity with the specified database entry ID
                }
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rowView = mLayoutInflater.inflate(R.layout.nswrow, parent);
        bindView(rowView, context, cursor);
        return rowView;
    }
    
    /**
     * Returns the date formatted appropriately for NSW
     * @param date
     * @return
     */
    private static String getFormattedDateForNSW(String date)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
