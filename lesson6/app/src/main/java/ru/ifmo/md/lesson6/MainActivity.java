package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "FEED_ACTIVITY";
    private static final int LOADER_ID = 1;
    private Intent intent;
    private ListView listView;
    private EditText editText;
    private SimpleCursorAdapter simpleCursorAdapter;
    private static final String[] from_feed = new String[] { MyDbHelper.COLUMN_FEED_TITLE };
    private static final int[] to_feed = new int[] { R.id.feed_title };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        intent = new Intent(this, FeedActivity.class);

        listView = (ListView) findViewById(R.id.listView);


        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.feed_row, null, from_feed, to_feed, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(simpleCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursor = simpleCursorAdapter.getCursor();
                cursor.moveToPosition(i);

                intent.putExtra(FeedActivity.INTENT_FEED_URL, cursor.getString(cursor.getColumnIndexOrThrow(MyDbHelper.COLUMN_FEED_LINK)));
                intent.putExtra(FeedActivity.INTENT_FEED_ID, cursor.getLong(cursor.getColumnIndexOrThrow(MyDbHelper.COLUMN_FEED_ID)));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = simpleCursorAdapter.getCursor();
                cursor.moveToPosition(i);
                deleteFeed(cursor.getLong(cursor.getColumnIndexOrThrow(MyDbHelper.COLUMN_FEED_ID)));
                return true;
            }
        });


    }

    private void deleteFeed(long feedId) {
        Uri delUri = ContentUris.withAppendedId(MyContentProvider.FEED_CONTENT_URI, feedId);
        getContentResolver().delete(delUri, null, null);
        getLoaderManager().restartLoader(LOADER_ID, null, this);

    }

    public void addRssFeed(View view) {
        FeedItem feedItem  = new FeedItem(editText.getText().toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDbHelper.COLUMN_FEED_LINK, feedItem.getLink());
        contentValues.put(MyDbHelper.COLUMN_FEED_TITLE, feedItem.getTitle());
        Uri newUri = getContentResolver().insert(MyContentProvider.FEED_CONTENT_URI, contentValues);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        Log.d(LOG_TAG, "new Uri: " + newUri.toString());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyFeedCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
