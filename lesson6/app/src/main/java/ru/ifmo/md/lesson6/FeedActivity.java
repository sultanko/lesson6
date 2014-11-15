package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class FeedActivity extends Activity {

    private ListView listView;
    private Intent intent;
    private MyBroadcastReceiver myBroadcastReceiver;
    private MyDbHelper myDbHelper;
    private long feedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        myDbHelper = new MyDbHelper(this);

        intent = new Intent(this, WebActivity.class);

        listView = (ListView) findViewById(R.id.listView);
        downloadFeeds();

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyListAdapter adapter = (MyListAdapter) adapterView.getAdapter();
                RssItem item = (RssItem) adapter.getItem(i);

                intent.putExtra("URL_PREVIEW", item.getLink());
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

        String feedUrls = getIntent().getStringExtra("URL_FEED");
        feedId = getIntent().getLongExtra("FEED_ID", -1);
        ArrayList<RssItem> items = myDbHelper.getAllRssItems(feedId);
        if (items == null) {
            Intent myService = new Intent(this, MyIntentService.class);
            startService(myService.putExtra(MyIntentService.INPUT_TAG, feedUrls));
        } else {
            MyListAdapter myListAdapter = new MyListAdapter(this, items);
            listView.setAdapter(myListAdapter);
        }

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<RssItem> result = (ArrayList<RssItem>) intent.getSerializableExtra(MyIntentService.RESULT_TAG);

            if (result == null) {
                String errorReason = intent.getStringExtra(MyIntentService.ERROR_TAG);
                Log.d("ACTIVITY: ", errorReason);
                Toast.makeText(getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
            } else {
                MyListAdapter myFeedAdapter = new MyListAdapter(getApplicationContext(), result);
                for (RssItem rssItem : result) {
                    rssItem.setFeedId(feedId);
                    myDbHelper.addRssItem(rssItem);
                }
                listView.setAdapter(myFeedAdapter);
            }
        }
    }

}
