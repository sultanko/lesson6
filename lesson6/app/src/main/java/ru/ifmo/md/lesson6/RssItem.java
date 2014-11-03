package ru.ifmo.md.lesson6;

/**
 * Created by sultan on 20.10.14.
 */
public class RssItem {

    private final String link;
    private final String title;
    private final String description;

    public RssItem(String link, String title, String description) {
        this.link = link;
        this.title = title;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
