package com.eltiland.model;

import com.eltiland.model.tags.ITagable;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Video item model.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "video", schema = "public")
public class Video extends AbstractIdentifiable implements ITagable {
    private String name;
    private String link;
    private String description;
    private int duration;
    private int viewCount;
    private Date creationDate;
    private User author;

    @Column(name = "link", nullable = false, length = 16)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "description", length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "duration")
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Column(name = "creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    @Transient
    public String getTabName() {
        return "Видео";
    }

    @Column(name = "view_count")
    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}