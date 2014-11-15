package ru.ifmo.md.lesson6;

import android.content.ContentValues;
import android.text.Html;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sultan on 20.10.14.
 */
public class RssItem  implements Serializable {

    private long id;
    private String link;
    private String title;
    private String description;
    private Date date;
    private long feedId;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public RssItem() {
    }

    public ContentValues toContentVales() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDbHelper.COLUMN_RSS_LINK, link);
        contentValues.put(MyDbHelper.COLUMN_RSS_TITLE, title);
        contentValues.put(MyDbHelper.COLUMN_RSS_DESCRIPTION, description);
        contentValues.put(MyDbHelper.COLUMN_RSS_DATE, date.toString());
        contentValues.put(MyDbHelper.COLUMN_RSS_FEED_ID, feedId);
        return contentValues;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {

        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {

        return id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = Html.fromHtml(description).toString();
    }

    public void setDate(String date) {
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            this.date = new Date();
            Log.d("Error:", "Parse date string: " + date);
        }
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
