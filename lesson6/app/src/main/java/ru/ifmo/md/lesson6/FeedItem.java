package ru.ifmo.md.lesson6;

/**
 * Created by sultan on 09.11.14.
 */
public class FeedItem {

    private long id;
    private String title;
    private String link;

    FeedItem() {}
    FeedItem(String link) {
        this.title = link;
        this.link = link;
    }
    FeedItem(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }
}
