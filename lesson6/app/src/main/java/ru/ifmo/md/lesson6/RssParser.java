package ru.ifmo.md.lesson6;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by sultan on 20.10.14.
 */
public class RssParser {

    public static ArrayList<RssItem> parseRss(String url) throws IOException, ParserConfigurationException, SAXException {
        URL uri = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        String contentType = connection.getHeaderField("Content-Type");
        String encoding = "utf-8";
        if (contentType != null && contentType.contains("charset=")) {
            Matcher matcher = Pattern.compile("charset=([^\\s]+)").matcher(contentType);
            matcher.find();
            encoding = matcher.group(1);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
        InputSource inputSource = new InputSource(inputStreamReader);


        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser parser = saxParserFactory.newSAXParser();
        SAXHandler saxHandler = new SAXHandler();

        parser.parse(inputSource, saxHandler);

        return  saxHandler.getResult();
    }

}
