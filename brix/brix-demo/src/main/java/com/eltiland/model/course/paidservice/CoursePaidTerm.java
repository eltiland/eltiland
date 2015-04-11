package com.eltiland.model.course.paidservice;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Table for term for the course paid invoice.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_paid_term", schema = "public")
public class CoursePaidTerm extends AbstractIdentifiable {
    private int years;
    private int months;
    private int days;

    @Column(name = "years", nullable = false, columnDefinition = "numeric default 0")
    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    @Column(name = "months", nullable = false, columnDefinition = "numeric default 0")
    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    @Column(name = "days", nullable = false, columnDefinition = "numeric default 0")
    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
