package ru.ifmo.md.lesson6;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sultan on 20.10.14.
 */
public class MyListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<RssItem> items;
    private final LayoutInflater mInflater;

    public MyListAdapter(Context context, ArrayList<RssItem> items) {
        this.mContext = context;
        this.items = items;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addRssItem(RssItem item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return (items == null ? 0 : items.size());
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = mInflater.inflate(R.layout.rss_row, viewGroup, false);
        }

        final RssItem item = (RssItem) getItem(i);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);

        title.setText(item.getTitle());
        description.setText(Html.fromHtml(item.getDescription()));
        return view;
    }

}
