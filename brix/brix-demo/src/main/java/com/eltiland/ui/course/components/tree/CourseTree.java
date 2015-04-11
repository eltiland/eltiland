package com.eltiland.ui.course.components.tree;


import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.*;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.tree.TreeModel;

/**
 * Tree control for Courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseTree extends ELTTree {

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    /**
     * Reference to the css file.
     */
    private static final ResourceReference CSS = new PackageResourceReference(
            CourseTree.class, "res/tree.css");

    /**
     * Reference to the icon of closed tree folder
     */
    private static final ResourceReference FOLDER_CLOSED =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_PARENT_CLOSED);

    /**
     * Reference to the icon of open tree folder
     */
    private static final ResourceReference FOLDER_OPEN =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_PARENT_OPEN);

    /**
     * Reference to the icon of tree item (not a folder)
     */
    private static final ResourceReference FOLDER_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_FOLDER);

    private static final ResourceReference TEST_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_TEST);

    private static final ResourceReference DOCUMENT_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_DOCUMENT);

    private static final ResourceReference VIDEO_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_VIDEO);

    private static final ResourceReference WEBINAR_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_WEBINAR);

    private static final ResourceReference PRESENTATION_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_PRESENTATION);

    private static final ResourceReference CONTROL_ITEM =
            new SharedResourceReference(ResourcesUtils.COURSE_CONTENT_CONTROL);


    /**
     * Default constructor.
     *
     * @param id    markup id.
     * @param model tree data model.
     */
    public CourseTree(String id, TreeModel model) {
        super(id, model);
        Injector.get().inject(this);
    }

    @Override
    protected String renderNode(ELTTreeNode node) {
        if (node instanceof VirtualCourseRootItem) {
            return ((VirtualCourseRootItem) node).getCourseName();
        } else if (node instanceof CourseItem) {
            if (!(nodeData.containsKey(node))) {
                nodeData.put(node, ((CourseItem) node).getId());
                genericManager.initialize(node, ((CourseItem) node).getChildren());

            }
            return ((CourseItem) node).getName();
        } else {
            return "";
        }
    }

    @Override
    protected ResourceReference getCSS() {
        return CSS;
    }

    /**
     * Returns the resource reference of default closed tree folder.
     *
     * @return The package resource reference
     */
    protected ResourceReference getFolderClosed() {
        return FOLDER_CLOSED;
    }

    /**
     * Returns the resource reference of default open tree folder.
     *
     * @return The package resource reference
     */
    protected ResourceReference getFolderOpen() {
        return FOLDER_OPEN;
    }

    @Override
    protected ResourceReference getNodeIcon(ELTTreeNode node) {
        if (node instanceof CourseItem && ((CourseItem) node).isControl()) {
            return CONTROL_ITEM;
        } else if (node instanceof FolderCourseItem) {
            return FOLDER_ITEM;
        } else if (node instanceof TestCourseItem) {
            return TEST_ITEM;
        } else if (node instanceof DocumentCourseItem) {
            return DOCUMENT_ITEM;
        } else if (node instanceof VideoCourseItem) {
            return VIDEO_ITEM;
        } else if (node instanceof WebinarCourseItem) {
            return WEBINAR_ITEM;
        } else if (node instanceof PresentationCourseItem) {
            return PRESENTATION_ITEM;
        } else {
            return FOLDER_CLOSED;
        }
    }

    @Override
    protected Component newJunctionLink(MarkupContainer parent, String id, String imageId, ELTTreeNode node) {
        if (!(node instanceof VirtualCourseRootItem) && ((CourseItem) node).getId() == null) {
            CourseItem currentItem = courseItemManager.getCourseItemById(getCurrendId());
            if (currentItem != null) {
                if (!(currentItem.getName().equals(((CourseItem) node).getName()))) {
                    node = courseItemManager.getItemByParentIdAndName(getCurrendId(), ((CourseItem) node).getName());
                }
            }
        }
        return super.newJunctionLink(parent, id, imageId, node);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript("formatTreeItem();");
    }
}
