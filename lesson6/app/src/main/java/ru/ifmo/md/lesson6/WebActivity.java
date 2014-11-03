package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by sultan on 20.10.14.
 */
public class WebActivity extends Activity {

    private WebView webView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = (WebView) findViewById(R.id.webView);
        intent = getIntent();
        String url_preview = intent.getStringExtra("URL_PREVIEW");
        webView.loadUrl(url_preview);
    }
}

