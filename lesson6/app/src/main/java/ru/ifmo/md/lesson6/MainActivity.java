package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ListView listView;
    private Intent intent;
    private EditText editText;
    private ArrayList<String> feeds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        intent = new Intent(this, WebActivity.class);

        listView = (ListView) findViewById(R.id.listView);
        feeds.add("http://feeds.bbci.co.uk/news/rss.xml");
        downloadFeeds();

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

    private void downloadFeeds() {

        String[] feedUrls = feeds.toArray(new String[0]);

        RssDownloader rssDownloader = new RssDownloader(this, listView);
        rssDownloader.execute(feedUrls);

    }

    public void addRssFeed(View view) {
        feeds.add(editText.getText().toString());
        downloadFeeds();
    }
}
