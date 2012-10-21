package com.bluebottlesoftware.nationalparkclosures.data;

import com.bluebottlesoftware.nationalparkclosures.Util.CalendarUtils;
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
 * Adapter which converts a dataset containing rows of feed data into a visible representation of the data
 */
public class FeedDataAdapter extends CursorAdapter
{
    public static final String FriendlyDateFormat = "E, dd MMM yyyy";       /**<Date format as we display in list*/
    
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
        
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView dateView  = (TextView) view.findViewById(R.id.date);
        TextView categoryView = (TextView) view.findViewById(R.id.category);
        
        String dateFormat = CalendarUtils.getDateFormatForState(cursor.getInt(mStateColumn));
        titleView.setText(title);
        dateView.setText(CalendarUtils.convertDate(date,dateFormat,FriendlyDateFormat));
        categoryView.setText(category);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rowView = mLayoutInflater.inflate(R.layout.feedlistrow, null);
        bindView(rowView, context, cursor);
        return rowView;
    }
}
