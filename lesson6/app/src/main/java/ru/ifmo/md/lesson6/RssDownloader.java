package ru.ifmo.md.lesson6;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by sultan on 20.10.14.
 */
public class RssDownloader extends AsyncTask<String, Void, ArrayList<RssItem> > {

    private final Context context;
    private final ListView listView;
    private ProgressDialog progressDialog;

    public RssDownloader(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Loading...");
        progressDialog.show();
    }


    @Override
    protected ArrayList<RssItem> doInBackground(String... strings) {


        try {
            ArrayList<RssItem> items = new ArrayList<RssItem>();

            for (String string : strings) {
                items.addAll(RssParser.parseRss(string));
            }

            return items;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (XmlPullParserException e) {
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<RssItem> rssItems) {
        super.onPostExecute(rssItems);
        progressDialog.hide();

        if (rssItems == null) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
        } else {
            MyListAdapter myListAdapter = new MyListAdapter(context, rssItems);
            listView.setAdapter(myListAdapter);
        }
    }

}
