package com.eltiland.model.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.file.File;
import com.eltiland.ui.course.components.tree.ELTTreeNode;
import com.eltiland.ui.course.components.tree.VirtualCourseRootItem;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Question/Answer entity
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "courseitem", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_prototype", discriminatorType = DiscriminatorType.STRING)
public abstract class CourseItem extends AbstractIdentifiable implements Serializable, ELTTreeNode {
    @SpringBean
    private GenericManager genericManager;

    private String name;
    private Course courseDemo;
    private Course courseFull;
    private CourseItem parentItem;
    private Set<CourseItem> children = new HashSet<>(0);
    private Set<File> files = new HashSet<>(0);
    private int index;
    private boolean control;
    private Date accessStartDate;
    private Date accessEndDate;

    @Transient
    private VirtualCourseRootItem root;

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "index", nullable = false)
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentItem")
    public CourseItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(CourseItem parent) {
        this.parentItem = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentItem")
    public Set<CourseItem> getChildren() {
        return children;
    }

    public void setChildren(Set<CourseItem> children) {
        this.children = children;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseDemo")
    public Course getCourseDemo() {
        return courseDemo;
    }

    public void setCourseDemo(Course courseDemo) {
        this.courseDemo = courseDemo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseFull")
    public Course getCourseFull() {
        return courseFull;
    }

    public void setCourseFull(Course courseFull) {
        this.courseFull = courseFull;
    }

    @Transient
    public Course getCourse() {
        Course course = getCourseDemo();

        if (course != null) {
            return course;
        } else {
            return getCourseFull();
        }
    }

    @Override
    @Transient
    public ELTTreeNode getChildAt(int childIndex) {
        for (CourseItem item : children) {
            if (item.getIndex() == childIndex) {
                return item;
            }
        }
        return null;
    }

    @Override
    @Transient
    public int getChildCount() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getChildren());
        return children.size();
    }

    @Override
    @Transient
    public int getIndex(ELTTreeNode node) {
        for (CourseItem item : children) {
            if (((CourseItem) node).getId().equals(item.getId())) {
                return item.getIndex();
            }
        }
        return -1;
    }

    @Override
    @Transient
    public boolean isLeaf() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getChildren());
        return children.isEmpty();
    }

    @Override
    @Transient
    public Enumeration children() {
        return (Enumeration) children;
    }

    @Transient
    public VirtualCourseRootItem getRoot() {
        return root;
    }

    @Transient
    public void setRoot(VirtualCourseRootItem root) {
        this.root = root;
    }

    @Override
    @Transient
    public ELTTreeNode getParent() {
        if (root != null) {
            return root;
        } else {
            return getParentItem();
        }
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    @Column(name = "control", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    @Column(name = "access_start")
    public Date getAccessStartDate() {
        return accessStartDate;
    }

    public void setAccessStartDate(Date accessStartDate) {
        this.accessStartDate = accessStartDate;
    }

    @Column(name = "access_end")
    public Date getAccessEndDate() {
        return accessEndDate;
    }

    public void setAccessEndDate(Date accessEndDate) {
        this.accessEndDate = accessEndDate;
    }
}
