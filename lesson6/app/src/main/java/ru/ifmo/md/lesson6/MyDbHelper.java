package ru.ifmo.md.lesson6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sultan on 10.11.14.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mysuperdb.db";
    private static final Integer VERSION = 2;

    public static final String TABLE_FEED = "feed";
    public static final String COLUMN_FEED_ID = "_id";
    public static final String COLUMN_FEED_TITLE = "title";
    public static final String COLUMN_FEED_LINK = "link";
    private static final String[] COLUMNS_FEED = {COLUMN_FEED_ID,
                                  COLUMN_FEED_TITLE, COLUMN_FEED_LINK};

    private static final String CREATE_TABLE_FEED =
            "create table " + TABLE_FEED + " ("
                    + COLUMN_FEED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FEED_TITLE + " TEXT, "
                    + COLUMN_FEED_LINK + " TEXT NOT NULL "
                    + " );";

    private static final FeedItem[] defaultFeeds = {
            new FeedItem("BBC", "http://feeds.bbci.co.uk/news/rss.xml"),
            new FeedItem("Эхо Москвы", "http://echo.msk.ru/interview/rss-fulltext.xml")};

    public static final String TABLE_RSS = "rss";
    public static final String COLUMN_RSS_ID = "_id";
    public static final String COLUMN_RSS_TITLE = "title";
    public static final String COLUMN_RSS_LINK = "link";
    public static final String COLUMN_RSS_DESCRIPTION = "description";
    public static final String COLUMN_RSS_DATE = "pub_date";
    public static final String COLUMN_RSS_FEED_ID = "feed_id";
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



}
