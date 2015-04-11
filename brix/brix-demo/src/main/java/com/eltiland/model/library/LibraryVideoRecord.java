package com.eltiland.model.library;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Video library entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("VIDEO")
public class LibraryVideoRecord extends LibraryRecord {
    private String videoLink;

    @Column(name = "video_link", length = 256)
    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }
}
