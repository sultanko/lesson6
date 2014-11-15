package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;


public class FeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private Intent intent;
    private MyBroadcastReceiver myBroadcastReceiver;
    private long feedId;
    private SimpleCursorAdapter simpleCursorAdapter;
    private String feedUrls;

    private static final String[] from_rss = new String[] { MyDbHelper.COLUMN_RSS_TITLE, MyDbHelper.COLUMN_RSS_DESCRIPTION};
    private static final int[] to_rss = new int[] { R.id.rss_title, R.id.rss_description };

    private static final int LOADER_ID = 1;

    private static final String LOG_TAG = "FEED_ACTIVITY";

    public static final String INTENT_FEED_URL = "FEED_URL";
    public static final String INTENT_FEED_ID = "FEED_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        intent = new Intent(this, WebActivity.class);

        listView = (ListView) findViewById(R.id.listView);
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.rss_row, null, from_rss, to_rss, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(simpleCursorAdapter);

        feedUrls = getIntent().getStringExtra(INTENT_FEED_URL);
        feedId = getIntent().getLongExtra(INTENT_FEED_ID, -1);
        Log.d(LOG_TAG, "start: " + "url " + feedUrls + " id: " + feedId);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursor = simpleCursorAdapter.getCursor();
                cursor.moveToPosition(i);

                intent.putExtra(WebActivity.INTENT_URL_FOR_VIEW, cursor.getString(cursor.getColumnIndexOrThrow(MyDbHelper.COLUMN_RSS_LINK)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    private void downloadFeeds() {
        Intent myService = new Intent(this, MyIntentService.class);
        startService(myService.putExtra(MyIntentService.INPUT_TAG, feedUrls));
    }

    public void refresh_feed(View view) {

        getContentResolver().delete(MyContentProvider.RSS_CONTENT_URI,
                MyDbHelper.COLUMN_RSS_FEED_ID + " = ?", new String[] {String.valueOf(feedId)});

        downloadFeeds();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyRssCursorLoader(this, feedId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            downloadFeeds();
        } else {
            simpleCursorAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        simpleCursorAdapter.swapCursor(null);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<RssItem> result = (ArrayList<RssItem>) intent.getSerializableExtra(MyIntentService.RESULT_TAG);

            if (result == null) {
                String errorReason = intent.getStringExtra(MyIntentService.ERROR_TAG);
                Log.d(LOG_TAG, errorReason);
                Toast.makeText(getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
            } else {
                for (RssItem item : result) {
                    item.setFeedId(feedId);
                    getContentResolver().insert(MyContentProvider.RSS_CONTENT_URI,
                            item.toContentVales());
                }
                Cursor cursor = getContentResolver().query(MyContentProvider.RSS_CONTENT_URI, null, MyDbHelper.COLUMN_RSS_FEED_ID + " = ?",
                        new String[] {String.valueOf(feedId)}, null);
                simpleCursorAdapter.swapCursor(cursor);
            }
        }
    }

}
