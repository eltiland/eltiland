package com.eltiland.model.course2.content.video;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Entity for separate video item in video course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "video_item", schema = "course")
public class ELTVideoItem extends AbstractIdentifiable {
    private ELTVideoCourseItem item;
    private String name;
    private String description;
    private String link;
    private Long index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    public ELTVideoCourseItem getItem() {
        return item;
    }

    public void setItem(ELTVideoCourseItem item) {
        this.item = item;
    }

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "link", nullable = false, length = 64)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }
}
