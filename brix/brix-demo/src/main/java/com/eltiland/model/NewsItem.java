package com.eltiland.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity represents one item of news.
 */
@Entity
@Table(name = "news_item", schema = "public")
public class NewsItem extends AbstractIdentifiable {
    /**
     * Title property maximum length.
     */
    public static final int TITLE_MAX_LENGTH = 255;

    private Date date;
    private String title;
    private String body;
    private boolean announcement;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "title", nullable = false, length = TITLE_MAX_LENGTH)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "body")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = "announcement")
    public boolean getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }
}
