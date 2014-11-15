package ru.ifmo.md.lesson6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sultan on 09.11.14.
 */
public class MyFeedAdapter extends BaseAdapter {

    private ArrayList<FeedItem> items;
    private LayoutInflater mInflater;

    public MyFeedAdapter(Context context, ArrayList<FeedItem> items) {
        this.items = items;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addFeedItem(FeedItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<FeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = mInflater.inflate(R.layout.rss_row, viewGroup, false);
        }

        final FeedItem item = (FeedItem) getItem(i);
        TextView title = (TextView) view.findViewById(R.id.feed_title);

        title.setText(item.getTitle());
        return view;
    }
}
