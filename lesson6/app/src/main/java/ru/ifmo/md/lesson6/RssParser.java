package ru.ifmo.md.lesson6;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by sultan on 20.10.14.
 */
public class RssParser {

    public static ArrayList<RssItem> parseRss(String url) throws IOException, ParserConfigurationException, SAXException {
        URL uri = new URL(url);

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        saxParserFactory.setValidating(true);
        SAXParser parser = saxParserFactory.newSAXParser();
        SAXHandler saxHandler = new SAXHandler();

        InputSource inputSource = new InputSource(url);

        parser.parse(inputSource, saxHandler);

        return  saxHandler.getResult();
    }

}
