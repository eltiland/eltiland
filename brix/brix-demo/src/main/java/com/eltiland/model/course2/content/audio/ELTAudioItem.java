package com.eltiland.model.course2.content.audio;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Entity for separate video item in video course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "audio_item", schema = "course")
public class ELTAudioItem extends AbstractIdentifiable {
    private ELTAudioCourseItem item;
    private String description;
    private String link;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public ELTAudioCourseItem getItem() {
        return item;
    }

    public void setItem(ELTAudioCourseItem item) {
        this.item = item;
    }

    @Column(name = "description", length = 2048)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "link", nullable = false, length = 128)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
