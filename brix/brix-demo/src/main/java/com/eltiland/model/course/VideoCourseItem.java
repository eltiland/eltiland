package com.eltiland.model.course;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Video course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("VIDEO")
public class VideoCourseItem extends ElementCourseItem {

    private String link;
    private Set<CourseVideoItem> videoItems = new HashSet<>(0);

    @Column(name = "link", length = 1024)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<CourseVideoItem> getVideoItems() {
        return videoItems;
    }

    public void setVideoItems(Set<CourseVideoItem> videoItems) {
        this.videoItems = videoItems;
    }
}
