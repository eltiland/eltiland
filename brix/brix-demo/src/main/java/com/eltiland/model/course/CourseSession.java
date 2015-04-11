package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for training course session time.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_session", schema = "public")
public class CourseSession extends AbstractIdentifiable {
    private Course course;
    private boolean active;
    private Date startDate;
    private Date finishDate;
    private Date prejoinDate;
    private boolean open;

    private Set<CourseListener> listenerSet = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Column(name = "active", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "startDate", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "finishDate", nullable = false)
    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    @Column(name = "preJoinDate", nullable = false)
    public Date getPrejoinDate() {
        return prejoinDate;
    }

    public void setPrejoinDate(Date prejoinDate) {
        this.prejoinDate = prejoinDate;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    public Set<CourseListener> getListenerSet() {
        return listenerSet;
    }

    public void setListenerSet(Set<CourseListener> listenerSet) {
        this.listenerSet = listenerSet;
    }

    @Column(name = "open")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
