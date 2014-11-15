package ru.ifmo.md.lesson6;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Created by sultan on 15.11.14.
 */
public class MyFeedCursorLoader extends CursorLoader {

    public MyFeedCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query(MyContentProvider.FEED_CONTENT_URI, null, null, null, null);
    }


}

