package com.eltiland.model.course.paidservice;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.payment.PaidEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Table for course paid invoices.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_paid_invoice", schema = "public")
public class CoursePaidInvoice extends AbstractIdentifiable implements Serializable, PaidEntity {
    private boolean status;
    private Date creationdate;
    private BigDecimal price;

    private Course course;
    private FolderCourseItem item;

    private Set<CoursePayment> payments = new HashSet<>(0);
    private CoursePaidTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public FolderCourseItem getItem() {
        return item;
    }

    public void setItem(FolderCourseItem item) {
        this.item = item;
    }

    @Override
    @Column(name = "status", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name = "creationdate", nullable = false)
    public Date getCreationDate() {
        return creationdate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationdate = creationDate;
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    public Set<CoursePayment> getPayments() {
        return payments;
    }

    public void setPayments(Set<CoursePayment> payments) {
        this.payments = payments;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term")
    public CoursePaidTerm getTerm() {
        return term;
    }

    public void setTerm(CoursePaidTerm term) {
        this.term = term;
    }
}
