package ru.ifmo.md.lesson6;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sultan on 20.10.14.
 */
public class RssParser {

    private enum RssTag {
        TITLE, DESCRIPTION, LINK, ITEM, OTHER;
    }

    public static ArrayList<RssItem> parseRss(String url) throws XmlPullParserException, IOException {
        InputStream inputStream = new URL(url).openStream();

        XmlPullParserFactory factory = XmlPullParserFactory .newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, null);

        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
        RssTag currentTag = RssTag.OTHER;

        int currentEvent = xpp.getEventType();
        String link = "";
        String title = "";
        String description = "";


        while (currentEvent != XmlPullParser.END_DOCUMENT) {

            if (currentEvent == XmlPullParser.START_DOCUMENT) {
                currentTag = RssTag.OTHER;

            } else if (currentEvent == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("link")) {
                    currentTag = RssTag.LINK;
                } else if (xpp.getName().equals("title")) {
                    currentTag = RssTag.TITLE;
                } else if (xpp.getName().equals("description")) {
                    currentTag = RssTag.DESCRIPTION;
                } else if (xpp.getName().equals("item")) {
                    link = "";
                    title = "";
                    description = "";
                    currentTag = RssTag.ITEM;
                } else {
                    currentTag = RssTag.OTHER;
                }
            } else if (currentEvent == XmlPullParser.END_TAG) {
                if (xpp.getName().equals("item")) {
                    RssItem rssItem = new RssItem(link, title, description);
                    rssItems.add(rssItem);
                }
            } else if (currentEvent == XmlPullParser.TEXT) {
                String text = xpp.getText();
                text.trim();

                switch (currentTag) {
                    case TITLE:
                        title += text;
                        break;
                    case LINK:
                        link += text;
                        break;
                    case DESCRIPTION:
                        description += text;
                        break;
                    default:
                        break;
                }
            }

            currentEvent = xpp.next();
        }

        return  rssItems;
    }

}
