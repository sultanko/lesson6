package ru.ifmo.md.lesson6;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by sultan on 09.11.14.
 */
public class SAXHandler extends DefaultHandler {

    private ArrayList<RssItem> result;
    private RssItem item;
    private String itemValue = null;
    private boolean itemElement = false;

    public ArrayList<RssItem> getResult() {
        return result;
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//        super.characters(ch, start, length);
//        Log.d("XML:", new String(ch, start, length));
        if (itemElement) {
            if (itemValue == null) {
                itemValue = new String(ch, start, length);
            } else {
                itemValue += (new String(ch, start, length));
            }
//            Log.d("XML: ", itemValue);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        super.endElement(uri, localName, qName);
//        Log.d("XML:", "End tag:" + localName);
        if (!itemElement) {
            itemValue = null;
            return;
        }
        if (localName.equalsIgnoreCase("item")) {
            itemElement = false;
            result.add(item);
        } else if (localName.equalsIgnoreCase("link")) {
            item.setLink(itemValue);
        } else if (localName.equalsIgnoreCase("title")) {
            item.setTitle(itemValue);
        } else if (localName.equalsIgnoreCase("description")) {
            item.setDescription(itemValue);
        } else if (localName.equalsIgnoreCase("pubDate")) {
            item.setDate(itemValue);
        }
        itemValue = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        super.startElement(uri, localName, qName, attributes);
        if (localName.equalsIgnoreCase("item")) {
            item = new RssItem();
            itemElement = true;
            itemValue = null;
        }
//        Log.d("XML:", "start tag:" + localName + itemElement);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
//        Log.d("XML:", "Document start");
        result = new ArrayList<RssItem>();
        itemElement = false;
    }
}
