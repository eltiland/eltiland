package com.eltiland.model.course2.listeners;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.ELTCourse;

import javax.persistence.*;

/**
 * Entity for data, required for registration into the course.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "user_data", schema = "course")
public class ELTCourseUserData extends AbstractIdentifiable {
    private ELTCourse course;
    private UserDataType type;
    private UserDataStatus status;
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course", nullable = false)
    public ELTCourse getCourse() {
        return course;
    }

    public void setCourse(ELTCourse course) {
        this.course = course;
    }

    @Column(name = "type", nullable = false, length = 11)
    @Enumerated(value = EnumType.STRING)
    public UserDataType getType() {
        return type;
    }

    public void setType(UserDataType type) {
        this.type = type;
    }

    @Column(name = "status", nullable = false, length = 11)
    @Enumerated(value = EnumType.STRING)
    public UserDataStatus getStatus() {
        return status;
    }

    public void setStatus(UserDataStatus status) {
        this.status = status;
    }

    @Column(name = "caption", length = 128)
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
