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

    public static final String TABLE_FEED = MyDbHelper.TABLE_FEED;
    public static final String TABLE_RSS = MyDbHelper.TABLE_RSS;

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

    private class QueryData {
        private String tableName;
        private String selection;


        public QueryData(Uri uri, String querySelection) {
            int uriType = mURIMatcher.match(uri);
            selection = querySelection;
            switch (uriType) {
                case URI_FEEDS:
                    tableName = TABLE_FEED;
                    break;
                case URI_FEEDS_ID:
                    tableName = TABLE_FEED;
                    String feed_id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(querySelection)) {
                        selection = MyDbHelper.COLUMN_FEED_ID + " = " + feed_id;
                    } else {
                        selection = querySelection + " AND " + MyDbHelper.COLUMN_FEED_ID + " = " + feed_id;
                    }
                    break;
                case URI_RSS:
                    tableName = TABLE_RSS;
                    break;
                case URI_RSS_ID:
                    tableName = TABLE_RSS;
                    String rss_id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(querySelection)) {
                        selection = MyDbHelper.COLUMN_FEED_ID + " = " + rss_id;
                    } else {
                        selection = querySelection + " AND " + MyDbHelper.COLUMN_FEED_ID + " = " + rss_id;
                    }
                    break;
            }

        }
        public String getSelection() {
            return selection;
        }

        public String getTableName() {
            return tableName;

        }
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
        Log.d(LOG_TAG, "start query: " + uri.toString() + " " + selection + " " + selectionArgs);

        int uriType = mURIMatcher.match(uri);
        QueryData data = new QueryData(uri, selection);
        Log.d(LOG_TAG, "query table: " + data.getTableName() + data.getSelection());
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(data.getTableName(), columns, data.getSelection(), selectionArgs, null, null, sortOrder);
        if (uriType == URI_FEEDS || uriType == URI_FEEDS_ID) {
            cursor.setNotificationUri(getContext().getContentResolver(), FEED_CONTENT_URI);
        } else {
            cursor.setNotificationUri(getContext().getContentResolver(), RSS_CONTENT_URI);
        }

        Log.d(LOG_TAG, "end query: " + uri.toString() + " " + selection + " " + selectionArgs);
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
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        QueryData data = new QueryData(uri, null);
        long rowId = db.insert(data.getTableName(), null, contentValues);

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
        QueryData data = new QueryData(uri, selection);

        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(data.getTableName(), data.getSelection(), selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "end delete: " + uri.toString());
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "start update: " + uri.toString());
        QueryData data = new QueryData(uri, selection);

        db = dbHelper.getWritableDatabase();
        int cnt = db.update(data.getTableName(), contentValues, data.getSelection(), selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "end update: " + uri.toString());
        return cnt;
    }
}
