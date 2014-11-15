package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by sultan on 20.10.14.
 */
public class WebActivity extends Activity {

    private WebView webView;
    private Intent intent;

    public static final String INTENT_URL_FOR_VIEW = "URL_FOR_VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = (WebView) findViewById(R.id.webView);
        intent = getIntent();
        String url_preview = intent.getStringExtra(INTENT_URL_FOR_VIEW);

        Log.d("WEB: ", url_preview);

        webView.loadUrl(url_preview);
    }
}

