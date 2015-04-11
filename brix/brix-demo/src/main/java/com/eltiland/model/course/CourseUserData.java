package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Course entity
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_user_data", schema = "public")
public class CourseUserData extends AbstractIdentifiable {

    public enum Type {COMPANY, JOB, ADDRESS, PHONE, EXPERIENCE}

    private Course course;
    private Type type;
    private boolean active;
    private boolean required;
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Column(name = "active", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "required", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Column(name = "caption", nullable = false, length = 128)
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
