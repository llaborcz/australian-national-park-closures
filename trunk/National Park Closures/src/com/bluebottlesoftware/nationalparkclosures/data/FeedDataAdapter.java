package com.bluebottlesoftware.nationalparkclosures.data;

import android.text.TextUtils;
import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
import com.bluebottlesoftware.nationalparkclosures.database.FeedDatabase;
import com.bluebottlesoftware.parkclosures.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Adapter which converts a dataset containing rows of feed data into a visible representation of the data
 */
public class FeedDataAdapter extends CursorAdapter
{
    public static final String DisplayDateFormat = "E, dd MMM yyyy" ;      /**<Date format as we display in list*/
    public static final String DisplayTimeFormat = "HH:mm";  /**<Format that we use for time*/
    private final String mLastUpdatedString;
    private LayoutInflater mLayoutInflater; // inflates our views
    private int mTitleColumn;       // Column index of title
    private int mDateColumn;        // Column index of date
    private int mCategoryColumn;    // Column index of category
    private int mStateColumn;       // Column index of state
    
    public FeedDataAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTitleColumn = c.getColumnIndex(FeedDatabase.COLUMN_TITLE);
        mDateColumn  = c.getColumnIndex(FeedDatabase.COLUMN_DATE);
        mCategoryColumn = c.getColumnIndex(FeedDatabase.COLUMN_CATEGORY);
        mStateColumn = c.getColumnIndex(FeedDatabase.COLUMN_REGION);
        mLastUpdatedString = context.getString(R.string.lastUpdated);
    }

    /**
     * Binds the data at the current row in the cursor to the given view, if the given view is null then 
     * a new view is created
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        String title = cursor.getString(mTitleColumn);
        String date  = cursor.getString(mDateColumn);
        String category = cursor.getString(mCategoryColumn);
        
        TextView titleView    = (TextView) view.findViewById(R.id.title);
        TextView dateView     = (TextView) view.findViewById(R.id.date);
        TextView categoryView = (TextView) view.findViewById(R.id.category);
        TextView timeView     = (TextView) view.findViewById(R.id.time);
        
        titleView.setText(title);
        String dateFormat = CalendarUtils.getDateFormatForRegion(cursor.getInt(mStateColumn));
        dateView.setText(CalendarUtils.convertDate(date,dateFormat,DisplayDateFormat));
        timeView.setText(mLastUpdatedString + CalendarUtils.convertDate(date, dateFormat, DisplayTimeFormat));
        if(TextUtils.isEmpty(category))
        {
            categoryView.setVisibility(View.GONE);
        }
        else
        {
            categoryView.setVisibility(View.VISIBLE);
            categoryView.setText(category);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rowView = mLayoutInflater.inflate(R.layout.feedlistrow, null);
        bindView(rowView, context, cursor);
        return rowView;
    }
}
