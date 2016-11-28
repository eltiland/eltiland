package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Webinar entity entity.
 */
@Entity
@Table(name = "webinar_event", schema = "public")
public class WebinarEvent extends AbstractIdentifiable {
    private String name;
    private Long   eventId;

    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "eventId", nullable = false)
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
