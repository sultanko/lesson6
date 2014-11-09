package ru.ifmo.md.lesson6;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by sultan on 20.10.14.
 */
public class RssParser {

    private class XMLHandler extends ContentHandler {

        @Override
        public Object getContent(URLConnection urlConnection) throws IOException {
            return null;
        }
    }

    private enum RssTag {
        TITLE, DESCRIPTION, LINK, ITEM, OTHER;
    }

    public static ArrayList<RssItem> parseRss(String url) throws XmlPullParserException, IOException, ParserConfigurationException, SAXException {
        InputStream inputStream = new URL(url).openStream();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser parser = saxParserFactory.newSAXParser();
        SAXHandler saxHandler = new SAXHandler();

        parser.parse(inputStream, saxHandler);

        return  saxHandler.getResult();
    }

}
