package com.eltiland.model.course2;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;

/**
 * M-M relation table for set of admins for the course.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "admins", schema = "course")
public class CourseAdmin extends AbstractIdentifiable {
    private User admin;
    private ELTCourse course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    public ELTCourse getCourse() {
        return course;
    }

    public void setCourse(ELTCourse course) {
        this.course = course;
    }
}
