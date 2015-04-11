package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Course video item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_video_item", schema = "public")
public class CourseVideoItem extends AbstractIdentifiable {
    private int index;
    private String name;
    private String description;
    private String link;
    private VideoCourseItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public VideoCourseItem getItem() {
        return item;
    }

    public void setItem(VideoCourseItem item) {
        this.item = item;
    }

    @Column(name = "index", nullable = false)
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 2048)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "link", length = 64)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
