package ru.ifmo.md.lesson6;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Created by sultan on 15.11.14.
 */
public class MyRssCursorLoader extends CursorLoader {

    private long feedId;

    public MyRssCursorLoader(Context context, long feedId) {
        super(context);
        this.feedId = feedId;
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query(MyContentProvider.RSS_CONTENT_URI, null,
                MyDbHelper.COLUMN_RSS_FEED_ID + " = ? ", new String[] {String.valueOf(feedId)}, null);
    }
}
