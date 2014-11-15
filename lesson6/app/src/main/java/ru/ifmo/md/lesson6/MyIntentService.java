package ru.ifmo.md.lesson6;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by sultan on 10.11.14.
 */
public class MyIntentService extends IntentService {

    public static final String RESULT_TAG = "RSS_ITEMS";
    public static final String INPUT_TAG = "URL_FEED";
    public static final String ERROR_TAG = "RSS_ERROR";
    public static final String ACTION_RESPONSE = "ru.ifmo.md.lesson6.intentservice.RESPONSE";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String feed = intent.getStringExtra(INPUT_TAG);
        Log.d("Intent: ", feed);
        String errorReason = new String();
        ArrayList<RssItem> result = null;
        try {
            result = RssParser.parseRss(feed);
        } catch (IOException e) {
            errorReason = new String("Network error");
        } catch (ParserConfigurationException e) {
            errorReason = new String("Parse error");
        } catch (SAXException e) {
            errorReason = new String("Parse error" + e.getMessage());
        }

        Intent response = new Intent();
        response.setAction(ACTION_RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra(RESULT_TAG, result);
        response.putExtra(ERROR_TAG, errorReason);
        sendBroadcast(response);
    }
}
