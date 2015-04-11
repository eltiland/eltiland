package com.eltiland.model.course2.content;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.IWithInterval;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Course block.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "block", schema = "course")
public class ELTCourseBlock extends AbstractIdentifiable implements IWithInterval {
    private ELTCourse course;
    private AuthorCourse demoCourse;
    private Integer index;
    private String name;
    private Date startDate;
    private Date endDate;
    private Boolean defaultAccess;

    private Set<ELTCourseItem> items = new HashSet<>(0);
    private Set<ELTCourseBlockAccess> blockAccessSet = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    public ELTCourse getCourse() {
        return course;
    }

    public void setCourse(ELTCourse course) {
        this.course = course;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demo_course")
    public AuthorCourse getDemoCourse() {
        return demoCourse;
    }

    public void setDemoCourse(AuthorCourse demoCourse) {
        this.demoCourse = demoCourse;
    }

    @Column(name = "index", nullable = false)
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Column(name = "name", nullable = false, length = 128)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "block")
    public Set<ELTCourseItem> getItems() {
        return items;
    }

    public void setItems(Set<ELTCourseItem> items) {
        this.items = items;
    }

    @Override
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "block")
    public Set<ELTCourseBlockAccess> getBlockAccessSet() {
        return blockAccessSet;
    }

    public void setBlockAccessSet(Set<ELTCourseBlockAccess> blockAccessSet) {
        this.blockAccessSet = blockAccessSet;
    }

    @Column(name = "access", nullable = false, columnDefinition = "boolean default FALSE")
    public Boolean getDefaultAccess() {
        return defaultAccess;
    }

    public void setDefaultAccess(Boolean defaultAccess) {
        this.defaultAccess = defaultAccess;
    }
}
