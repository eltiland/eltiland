package com.eltiland.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Eltiland DB properties.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "property", schema = "public")
public class Property extends AbstractIdentifiable {
    private String property;
    private String value;

    // Means count of videos on page.
    public static final String VIDEO_PAGING = "video_count";
    public static final String SHOW_SLIDER = "show_slider";

    @Column(name = "property", nullable = false)
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
