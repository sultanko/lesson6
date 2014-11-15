package ru.ifmo.md.lesson6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by sultan on 10.11.14.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mysuperdb.db";
    private static final Integer VERSION = 1;

    public static final String TABLE_FEED = "feed";
    public static final String COLUMN_FEED_ID = "_id";
    private static final String COLUMN_FEED_TITLE = "title";
    private static final String COLUMN_FEED_LINK = "link";
    private static final String[] COLUMNS_FEED = {COLUMN_FEED_ID,
                                  COLUMN_FEED_TITLE, COLUMN_FEED_LINK};

    private static final String CREATE_TABLE_FEED =
            "create table " + TABLE_FEED + " ("
                    + COLUMN_FEED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FEED_TITLE + " TEXT, "
                    + COLUMN_FEED_LINK + " TEXT NOT NULL "
                    + " );";

    private static final FeedItem[] defaultFeeds = { new FeedItem("BBC", "http://feeds.bbci.co.uk/news/rss.xml")};

    public static final String TABLE_RSS = "rss";
    public static final String COLUMN_RSS_ID = "_id";
    private static final String COLUMN_RSS_TITLE = "title";
    private static final String COLUMN_RSS_LINK = "link";
    private static final String COLUMN_RSS_DESCRIPTION = "description";
    private static final String COLUMN_RSS_DATE = "pub_date";
    private static final String COLUMN_RSS_FEED_ID = "feed_id";
    private static final String[] COLUMNS_RSS = {
            COLUMN_RSS_ID, COLUMN_RSS_TITLE,
            COLUMN_RSS_LINK, COLUMN_RSS_DESCRIPTION,
            COLUMN_RSS_DATE, COLUMN_RSS_FEED_ID
    };

    private static final String CREATE_TABLE_RSS =
            "create table " + TABLE_RSS + " ("
                    + COLUMN_RSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_RSS_TITLE + " TEXT NOT NULL, "
                    + COLUMN_RSS_LINK + " TEXT NOT NULL, "
                    + COLUMN_RSS_DESCRIPTION + " TEXT NOT NULL, "
                    + COLUMN_RSS_DATE + " TEXT NOT NULL, "
                    + COLUMN_RSS_FEED_ID + " INTEGER "
                    + " );";

    private SQLiteDatabase database;


    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        database = getWritableDatabase();
        setDefaultFeeds();
    }

    private void setDefaultFeeds() {
        Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + TABLE_FEED, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                for (FeedItem feedItem : defaultFeeds) {
                    addFeed(feedItem);
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_FEED);
        sqLiteDatabase.execSQL(CREATE_TABLE_RSS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);
        onCreate(sqLiteDatabase);
    }

    public void addFeed(FeedItem feed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FEED_LINK, feed.getLink());
        contentValues.put(COLUMN_FEED_TITLE, feed.getTitle());
        feed.setId(database.insert(TABLE_FEED, null, contentValues));
    }

    public ArrayList<FeedItem> getAllFeeds() {
        ArrayList<FeedItem> feeds = new ArrayList<FeedItem>();

        Cursor cursor = database.query(TABLE_FEED, COLUMNS_FEED, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FeedItem feedItem = new FeedItem();
            feedItem.setId(cursor.getLong(0));
            feedItem.setTitle(cursor.getString(1));
            feedItem.setLink(cursor.getString(2));
            feeds.add(feedItem);
            cursor.moveToNext();
        }
        cursor.close();

        return feeds;
    }

    public void addRssItem(RssItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RSS_LINK, item.getLink());
        contentValues.put(COLUMN_RSS_TITLE, item.getTitle());
        contentValues.put(COLUMN_RSS_DESCRIPTION, item.getDescription());
        contentValues.put(COLUMN_RSS_DATE, item.getDate().toString());
        contentValues.put(COLUMN_RSS_FEED_ID, item.getFeedId());
        database.insert(TABLE_RSS, null, contentValues);
    }

    public ArrayList<RssItem> getAllRssItems(long feedId) {
       ArrayList<RssItem> items = new ArrayList<RssItem>();

        Cursor cursor = database.query(TABLE_RSS, COLUMNS_RSS, COLUMN_RSS_FEED_ID + " = " + feedId, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }

        while (!cursor.isAfterLast()) {
            RssItem rssItem = new RssItem();
            rssItem.setId(cursor.getLong(0));
            rssItem.setTitle(cursor.getString(1));
            rssItem.setLink(cursor.getString(2));
            rssItem.setDescription(cursor.getString(3));
            rssItem.setDate(cursor.getString(4));
            rssItem.setFeedId(cursor.getLong(5));
            items.add(rssItem);
            cursor.moveToNext();
        }
        cursor.close();

        return items;

    }

}
