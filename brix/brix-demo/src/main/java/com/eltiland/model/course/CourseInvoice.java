package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.file.File;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Course entity
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "courseinvoice", schema = "public")
public class CourseInvoice extends AbstractIdentifiable {
    private User listener;
    private Course course;
    private Date creationDate;
    private boolean apply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener")
    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Column(name = "creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "apply", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isApply() {
        return apply;
    }

    public void setApply(boolean apply) {
        this.apply = apply;
    }
}
