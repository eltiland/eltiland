package com.eltiland.ui.course.components.editPanels;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.CourseManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.*;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.loadingPanel.ELTLoadingPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.editPanels.elements.*;
import com.eltiland.ui.course.components.editPanels.elements.test.TestEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for editing content of course.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseContentEditPanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentEditPanel.class);

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;

    private Course.CONTENT_KIND kind;

    private IModel<CourseItem> currentItemModel = new GenericDBModel<>(CourseItem.class);
    private WebMarkupContainer editContainer = new WebMarkupContainer("editContainer");

    private Label headerLabel = new Label("header", "") {
        @Override
        public boolean isVisible() {
            CourseItem item = currentItemModel.getObject();
            return item != null && !(item instanceof FolderCourseItem);
        }
    };

    private WebMarkupContainer preJoinContainer = new WebMarkupContainer("preJoinContainer") {
        @Override
        public boolean isVisible() {
            Course course = CourseContentEditPanel.this.getModelObject();
            genericManager.initialize(course, course.getFullVersion());
            return kind.equals(Course.CONTENT_KIND.FULL) && course.getFullVersion().isEmpty();
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id
     * @param courseIModel course model.
     */
    public CourseContentEditPanel(String id, final IModel<Course> courseIModel, final Course.CONTENT_KIND kind) {
        super(id, courseIModel);
        this.kind = kind;
        add(editContainer.setOutputMarkupPlaceholderTag(true));
        editContainer.add(new EmptyEditPanel("editPanel"));

        preJoinContainer.setOutputMarkupId(true);

        final Label preJoinInfo = new Label("preJoinInfo",
                getString(courseIModel.getObject().isPreJoin() ? "prejoin.open" : "prejoin.closed"));

        EltiAjaxLink openJoinButton = new EltiAjaxLink("openJoinButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Course course = CourseContentEditPanel.this.getModelObject();
                course.setPreJoin(true);
                preJoinInfo.setDefaultModelObject(getString("prejoin.open"));
                target.add(preJoinContainer);

                try {
                    courseManager.updateCourse(course);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update course item", e);
                    throw new WicketRuntimeException("Cannot update course item", e);
                }
            }

            @Override
            public boolean isVisible() {
                return !(courseIModel.getObject().isPreJoin());
            }
        };

        EltiAjaxLink closeJoinButton = new EltiAjaxLink("closeJoinButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Course course = CourseContentEditPanel.this.getModelObject();
                course.setPreJoin(false);
                preJoinInfo.setDefaultModelObject(getString("prejoin.closed"));
                target.add(preJoinContainer);

                try {
                    courseManager.updateCourse(course);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update course item", e);
                    throw new WicketRuntimeException("Cannot update course item", e);
                }
            }

            @Override
            public boolean isVisible() {
                return courseIModel.getObject().isPreJoin();
            }
        };

        preJoinContainer.add(preJoinInfo);
        preJoinContainer.add(openJoinButton);
        preJoinContainer.add(closeJoinButton);
        add(preJoinContainer);

        editContainer.add(headerLabel.setOutputMarkupPlaceholderTag(true));
    }

    public void updateCurrentItem(AjaxRequestTarget target, final CourseItem item, final Long id) {
        if (item instanceof GoogleCourseItem) {
            editContainer.replace(new ELTLoadingPanel("editPanel") {
                @Override
                public Component getLazyLoadComponent(String markupId) {
                    return new GoogleEditPanel(markupId,
                            new GenericDBModel<>(GoogleCourseItem.class, (GoogleCourseItem) item)) {
                        @Override
                        protected void onSave(AjaxRequestTarget target) {
                            updateTree(target);
                        }
                    };
                }
            });
        } else if (item instanceof TestCourseItem) {
            editContainer.replace(new ELTLoadingPanel("editPanel") {
                @Override
                public Component getLazyLoadComponent(String markupId) {
                    return new TestEditPanel(markupId,
                            new GenericDBModel<>(TestCourseItem.class, (TestCourseItem) item));
                }
            });
        } else if (item instanceof VideoCourseItem) {

            final CourseItem tItem = courseItemManager.getCourseItemById(item.getId());
            editContainer.replace(new ELTLoadingPanel("editPanel") {
                @Override
                public Component getLazyLoadComponent(String markupId) {
                    return new VideoEditPanel(
                            markupId, new GenericDBModel<>(VideoCourseItem.class, id));
                }
            });
        } else if (item instanceof WebinarCourseItem) {
            final CourseItem tItem = courseItemManager.getCourseItemById(item.getId());
            editContainer.replace(new ELTLoadingPanel("editPanel") {
                @Override
                public Component getLazyLoadComponent(String markupId) {
                    return new WebinarEditPanel(
                            markupId, new GenericDBModel<>(WebinarCourseItem.class, (WebinarCourseItem) tItem));
                }
            });
        } else {
            editContainer.replace(new EmptyEditPanel("editPanel"));
        }

        currentItemModel.setObject(item);
        if (currentItemModel.getObject() != null) {
            headerLabel.setDefaultModelObject(
                    String.format(getString("header"), currentItemModel.getObject().getName()));
        }
        target.add(editContainer);
        target.add(headerLabel);
    }

    public void cleanPanel(AjaxRequestTarget target) {
        editContainer.replace(new EmptyEditPanel("editPanel"));
        target.add(editContainer);
        headerLabel.setVisible(false);
        currentItemModel.setObject(null);
        target.add(headerLabel);
    }

    public void updatePreJoinContainer(AjaxRequestTarget target) {
        target.add(preJoinContainer);
    }

    public abstract void updateTree(AjaxRequestTarget target);
}
