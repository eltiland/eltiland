package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Webinar entity entity.
 */
@Entity
@Table(name = "webinar_event", schema = "public")
public class WebinarEvent extends AbstractIdentifiable {
    private String name;
    private Long   eventId;
    private Set<Webinar> webinars = new HashSet<>(0);

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    public Set<Webinar> getWebinars() {
        return webinars;
    }

    public void setWebinars(Set<Webinar> webinars) {
        this.webinars = webinars;
    }
}
