package ru.ifmo.md.lesson6;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by sultan on 10.11.14.
 */
public class MyContentProvider extends ContentProvider {

    private String LOG_TAG = "CONTENT_PROVIDER: ";

    public static final String TABLE_FEED = "feed";
    public static final String COLUMN_FEED_ID = "_id";
    public static final String COLUMN_FEED_TITLE = "title";
    public static final String COLUMN_FEED_LINK = "link";
    private static final String[] COLUMNS_FEED = {COLUMN_FEED_ID,
            COLUMN_FEED_TITLE, COLUMN_FEED_LINK};

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

    private static final String AUTHORITY = "ru.ifmo.md.lesson6.contentprovider";

    private static final String FEED_PATH = "feed";
    private static final String RSS_PATH = "rss";

    public static final Uri FEED_CONTENT_URI = Uri.parse("content://" + AUTHORITY
                            + "/" + FEED_PATH);

    public static final String FEED_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + AUTHORITY
           + "." + FEED_PATH;

    public static final String FEED_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + AUTHORITY
            + "." + FEED_PATH;

    public static final Uri RSS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + RSS_PATH);

    public static final String RSS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + AUTHORITY
            + "." + RSS_PATH;

    public static final String RSS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + AUTHORITY
            + "." + RSS_PATH;

    private static final int URI_FEEDS = 1;
    private static final int URI_FEEDS_ID = 2;
    private static final int URI_RSS = 3;
    private static final int URI_RSS_ID = 4;

    private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mURIMatcher.addURI(AUTHORITY, FEED_PATH, URI_FEEDS);
        mURIMatcher.addURI(AUTHORITY, FEED_PATH + "/#", URI_FEEDS_ID);
        mURIMatcher.addURI(AUTHORITY, RSS_PATH, URI_RSS);
        mURIMatcher.addURI(AUTHORITY, RSS_PATH + "/#", URI_RSS_ID);
    }

    private MyDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new MyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "start query: " + uri.toString());

        int uriType = mURIMatcher.match(uri);
        String table_name = "";
        switch (uriType) {
            case URI_FEEDS:
                table_name = TABLE_FEED;
                break;
            case URI_FEEDS_ID:
                table_name = TABLE_FEED;
                String feed_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + feed_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + feed_id;
                }
                break;
            case URI_RSS:
                table_name = TABLE_RSS;
                break;
            case URI_RSS_ID:
                table_name = TABLE_RSS;
                String rss_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + rss_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + rss_id;
                }
                break;
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table_name, columns, selection, selectionArgs, null, null, sortOrder);
        if (uriType == URI_FEEDS || uriType == URI_FEEDS_ID) {
            cursor.setNotificationUri(getContext().getContentResolver(), FEED_CONTENT_URI);
        } else {
            cursor.setNotificationUri(getContext().getContentResolver(), RSS_CONTENT_URI);
        }

        Log.d(LOG_TAG, "end query: " + uri.toString());
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        switch (mURIMatcher.match(uri)) {
            case URI_FEEDS:
                return FEED_CONTENT_TYPE;
            case URI_FEEDS_ID:
                return FEED_ITEM_TYPE;
            case URI_RSS:
                return RSS_CONTENT_TYPE;
            case URI_RSS_ID:
                return RSS_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Log.d(LOG_TAG, "start insert: " + uri.toString());
        int uriType = mURIMatcher.match(uri);
        if (uriType != URI_FEEDS && uriType != URI_RSS) {
//            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowId;
        if (uriType == URI_FEEDS) {
            rowId = db.insert(TABLE_FEED, null, contentValues);
        } else {
            rowId = db.insert(TABLE_RSS, null, contentValues);
        }

        Uri resultUri;
        if (uriType == URI_FEEDS) {
            resultUri = ContentUris.withAppendedId(FEED_CONTENT_URI, rowId);
        } else {
            resultUri = ContentUris.withAppendedId(RSS_CONTENT_URI, rowId);
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        Log.d(LOG_TAG, "end insert: " + uri.toString());
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "start delete: " + uri.toString());
        int uriType = mURIMatcher.match(uri);
        String table_name = "";
        switch (uriType) {
            case URI_FEEDS:
                table_name = TABLE_FEED;
                break;
            case URI_FEEDS_ID:
                table_name = TABLE_FEED;
                String feed_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + feed_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + feed_id;
                }
                break;
            case URI_RSS:
                table_name = TABLE_RSS;
                break;
            case URI_RSS_ID:
                table_name = TABLE_RSS;
                String rss_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + rss_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + rss_id;
                }
                break;
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(table_name, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "end delete: " + uri.toString());
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "start update: " + uri.toString());
        int uriType = mURIMatcher.match(uri);
        String table_name = "";
        switch (uriType) {
            case URI_FEEDS:
                table_name = TABLE_FEED;
                break;
            case URI_FEEDS_ID:
                table_name = TABLE_FEED;
                String feed_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + feed_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + feed_id;
                }
                break;
            case URI_RSS:
                table_name = TABLE_RSS;
                break;
            case URI_RSS_ID:
                table_name = TABLE_RSS;
                String rss_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_FEED_ID + " = " + rss_id;
                } else {
                    selection = selection + " AND " + COLUMN_FEED_ID + " = " + rss_id;
                }
                break;
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.update(table_name, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "end update: " + uri.toString());
        return cnt;
    }
}
