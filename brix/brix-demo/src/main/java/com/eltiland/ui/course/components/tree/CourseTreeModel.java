package com.eltiland.ui.course.components.tree;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.Serializable;

/**
 * Tree model for Courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseTreeModel implements TreeModel, Serializable {

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    private boolean isDemo;

    private IModel<Course> courseModel = new GenericDBModel<>(Course.class);

    /**
     * Model constructor.
     *
     * @param course Course item.
     * @param isDemo if TRUE - will display DEMO version.
     */
    public CourseTreeModel(Course course, boolean isDemo) {
        Injector.get().inject(this);
        this.isDemo = isDemo;

        if (isDemo) {
            genericManager.initialize(course, course.getDemoVersion());
        } else {
            genericManager.initialize(course, course.getFullVersion());
        }
        courseModel.setObject(course);
    }

    @Override
    public Object getRoot() {
        Course course = courseModel.getObject();
        VirtualCourseRootItem parent = new VirtualCourseRootItem(isDemo, course);

        for (CourseItem item : isDemo ? course.getDemoVersion() : course.getFullVersion()) {
            item.setRoot(parent);
        }
        return parent;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof VirtualCourseRootItem) {
            return ((VirtualCourseRootItem) parent).getChildAt(index);
        }

        if (parent instanceof ELTTreeNode) {
            CourseItem item = (CourseItem) ((ELTTreeNode) parent).getChildAt(index);
            genericManager.initialize(item, item.getChildren());
            return item;
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof ELTTreeNode) {
            return ((ELTTreeNode) parent).getChildCount();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof ELTTreeNode && ((ELTTreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }

        if ((parent instanceof ELTTreeNode) && (child instanceof ELTTreeNode)) {
            return ((ELTTreeNode) parent).getIndex((ELTTreeNode) child);
        } else {
            return -1;
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
