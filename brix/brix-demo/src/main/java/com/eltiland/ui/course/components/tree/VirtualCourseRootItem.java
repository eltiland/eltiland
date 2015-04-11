package com.eltiland.ui.course.components.tree;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Set;

/**
 * Virtual root item for demo and full versions of the course.
 *
 * @author Aleksey Plotnikov
 */
public class VirtualCourseRootItem implements ELTTreeNode, Serializable {

    @SpringBean
    private GenericManager genericManager;

    private boolean isDemo = false;

    private IModel<Course> courseModel = new GenericDBModel<>(Course.class);

    private IModel<Set<CourseItem>> itemsModel = new LoadableDetachableModel<Set<CourseItem>>() {
        @Override
        protected Set<CourseItem> load() {
            if (isDemo) {
                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getDemoVersion());
                return courseModel.getObject().getDemoVersion();
            } else {
                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getFullVersion());
                return courseModel.getObject().getFullVersion();
            }
        }
    };

    /**
     * Item constructor.
     *
     * @param isDemo true, if demo-version will be show in tree.
     * @param course course item
     */
    public VirtualCourseRootItem(boolean isDemo, Course course) {
        Injector.get().inject(this);
        genericManager.initialize(course, course.getDemoVersion());
        genericManager.initialize(course, course.getFullVersion());
        this.isDemo = isDemo;
        courseModel.setObject(course);
    }

    @Override
    public ELTTreeNode getChildAt(int childIndex) {
        for (CourseItem item : itemsModel.getObject()) {
            if (item.getIndex() == childIndex) {
                return item;
            }
        }
        return null;
    }

    @Override
    public int getChildCount() {
        return itemsModel.getObject().size();
    }

    @Override
    public ELTTreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(ELTTreeNode node) {
        for (CourseItem item : itemsModel.getObject()) {
            if (((CourseItem) node).getId().equals(item.getId())) {
                return item.getIndex();
            }
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return itemsModel.getObject().isEmpty();
    }

    @Override
    public Enumeration children() {
        return (Enumeration) itemsModel.getObject();
    }

    /**
     * @return name of course.
     */
    public String getCourseName() {
        return courseModel.getObject().getName();
    }

    public Course getCourseObject() {
        return courseModel.getObject();
    }
}
