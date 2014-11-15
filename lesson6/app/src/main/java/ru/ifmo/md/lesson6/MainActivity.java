package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends Activity {

    private String LOG_TAG = "FEED_ACTIVITY";
    private Intent intent;
    private ListView listView;
    private EditText editText;
/*    private ArrayList<FeedItem> feeds = new ArrayList<FeedItem>();
    private MyFeedAdapter myFeedAdapter;
    private MyDbHelper myDbHelper;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        intent = new Intent(this, FeedActivity.class);

        listView = (ListView) findViewById(R.id.listView);

        String[] from_feed = new String[] { MyContentProvider.COLUMN_FEED_TITLE };
        int[] to_feed = new int[] { R.id.feed_title };

        final Cursor cursor = getContentResolver().query(MyContentProvider.FEED_CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.feed_row, cursor, from_feed, to_feed);

/*        myDbHelper = new MyDbHelper(this);

        feeds = myDbHelper.getAllFeeds();
        myFeedAdapter = new MyFeedAdapter(this, feeds);*/

        listView.setAdapter(simpleCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                cursor.moveToPosition(i);
//                int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COLUMN_FEED_ID));
/*                MyFeedAdapter adapter = (MyFeedAdapter) adapterView.getAdapter();
                FeedItem item = (FeedItem) adapter.getItem(i);*/

                intent.putExtra("URL_FEED", cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.COLUMN_FEED_LINK)));
                intent.putExtra("FEED_ID", cursor.getInt(cursor.getColumnIndexOrThrow(MyContentProvider.COLUMN_FEED_ID)));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                Uri delUri = ContentUris.withAppendedId(MyContentProvider.FEED_CONTENT_URI,
                        cursor.getLong(cursor.getColumnIndexOrThrow(MyContentProvider.COLUMN_FEED_ID)));
                getContentResolver().delete(delUri, null, null);
                return true;
            }
        });

    }

    public void addRssFeed(View view) {
        FeedItem feedItem  = new FeedItem(editText.getText().toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyContentProvider.COLUMN_FEED_LINK, feedItem.getLink());
        contentValues.put(MyContentProvider.COLUMN_FEED_TITLE, feedItem.getTitle());
        Uri newUri = getContentResolver().insert(MyContentProvider.FEED_CONTENT_URI, contentValues);
        Log.d(LOG_TAG, "new Uri: " + newUri.toString());

/*        myDbHelper.addFeed(feedItem);
        myFeedAdapter.addFeedItem(feedItem);*/
    }

}
