package ru.ifmo.md.lesson6;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sultan on 20.10.14.
 */
public class RssItem {

    private String link;
    private String title;
    private String description;
    private Date date;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    public RssItem() {
    }

    public RssItem(String link, String title, String description, String date) {
        this.link = link;
        this.title = title;
        this.description = description;
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            this.date = new Date();
            Log.d("Error:", "Parse date string: " + date);
        }
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
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
