package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CourseManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.course.CourseControlPage;
import com.eltiland.ui.course.CourseControlPage_old;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Courses control management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CourseControlManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;

    private final DataGrid<CourseDataSource, Course> grid;

    public CourseControlManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<CourseDataSource, Course>> columns = new ArrayList<>();

        columns.add(new PropertyWrapColumn(new ResourceModel("dateLabel"), "creationDate", "creationDate"));
        columns.add(new PropertyWrapColumn(new ResourceModel("nameLabel"), "name", "name") {
            @Override
            public int getInitialSize() {
                return 210;
            }
        });

        columns.add(new AbstractColumn<CourseDataSource, Course>("statusLabel",
                new ResourceModel("statusLabel"), "published") {
            @Override
            public Component newCell(WebMarkupContainer components, String s, IModel<Course> courseIModel) {
                return new CourseStatusPanel(s, courseIModel) {
                    @Override
                    public void onPublish(AjaxRequestTarget target) {
                        target.add(grid);
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        target.add(grid);
                    }
                };
            }
        });

        columns.add(new AbstractColumn<CourseDataSource, Course>("actionId", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, IModel<Course> rowModel) {
                return new CourseControlPanel(componentId, rowModel);
            }
        });

        columns.add(new AbstractColumn<CourseDataSource, Course>("accessId", new ResourceModel("accessLabel")) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, IModel<Course> rowModel) {
                return new CourseAccessPanel(componentId, rowModel);
            }

            @Override
            public int getInitialSize() {
                return 220;
            }
        });

        grid = new DefaultDataGrid<>("grid", new Model<CourseDataSource>(new CourseDataSource() {
            @Override
            public boolean getCourseStatus() {
                return true;
            }

            @Override
            public CourseManager getManager() {
                return courseManager;
            }
        }), columns);
        add(grid.setOutputMarkupId(true));
    }

    private abstract class CourseStatusPanel extends BaseEltilandPanel<Course> {

        private final Logger LOGGER = LoggerFactory.getLogger(CourseStatusPanel.class);

        protected CourseStatusPanel(String id, final IModel<Course> courseIModel) {
            super(id, courseIModel);

            add(new EltiAjaxLink("publish") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    Course course = courseIModel.getObject();
                    course.setPublished(true);
                    try {
                        courseManager.updateCourse(course);
                        ELTAlerts.renderOKPopup(getString("publishMessage"), target);
                        onPublish(target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot publish course", e);
                        throw new WicketRuntimeException("Cannot publish course", e);
                    }
                }

                @Override
                public boolean isVisible() {
                    return !(courseIModel.getObject().isPublished()) && courseIModel.getObject().isStatus();
                }
            });

            add(new EltiAjaxLink("cancel") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    Course course = courseIModel.getObject();
                    course.setPublished(false);
                    try {
                        courseManager.updateCourse(course);
                        ELTAlerts.renderOKPopup(getString("cancelMessage"), target);
                        onCancel(target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot unpublish course", e);
                        throw new WicketRuntimeException("Cannot unpublish course", e);
                    }
                }

                @Override
                public boolean isVisible() {
                    return courseIModel.getObject().isPublished() && courseIModel.getObject().isStatus();
                }
            });
        }

        public abstract void onPublish(AjaxRequestTarget target);

        public abstract void onCancel(AjaxRequestTarget target);
    }

    private class CourseControlPanel extends BaseEltilandPanel<Course> {

        public CourseControlPanel(String id, IModel<Course> courseIModel) {
            super(id, courseIModel);
            add(new BookmarkablePageLink("control", CourseControlPage_old.class,
                    new PageParameters().add(CourseControlPage_old.PARAM_ID, courseIModel.getObject().getId())));
        }
    }

    private class CourseAccessPanel extends BaseEltilandPanel<Course> {

        private final Logger LOGGER = LoggerFactory.getLogger(CourseStatusPanel.class);

        private IModel<String> valueModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                boolean isAutoJoin = getModelObject().isAutoJoin();
                return getString(isAutoJoin ? "no" : "yes");
            }
        };

        private IModel<String> descriptionModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                boolean isAutoJoin = getModelObject().isAutoJoin();
                return getString(isAutoJoin ? "descriptionAuto" : "descriptionSubmit");
            }
        };

        private IModel<Boolean> controlAvailableModel = new LoadableDetachableModel<Boolean>() {
            @Override
            protected Boolean load() {
                genericManager.initialize(CourseAccessPanel.this.getModelObject(),
                        CourseAccessPanel.this.getModelObject().getFullVersion());
                return !(CourseAccessPanel.this.getModelObject().getFullVersion().isEmpty());
            }
        };

        private Label value = new Label("value", valueModel);
        private Label description = new Label("description", descriptionModel);

        private EltiAjaxLink onButton = new EltiAjaxLink("onButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("onConfirmation")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                changeStatus(false);
                valueModel.detach();
                target.add(value);
                target.add(description);
                target.add(onButton);
                target.add(offButton);
            }

            @Override
            public boolean isVisible() {
                return CourseAccessPanel.this.getModelObject().isAutoJoin();
            }
        };

        private EltiAjaxLink offButton = new EltiAjaxLink("offButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("offConfirmation")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                changeStatus(true);
                valueModel.detach();
                target.add(value);
                target.add(description);
                target.add(onButton);
                target.add(offButton);
            }

            @Override
            public boolean isVisible() {
                return !(CourseAccessPanel.this.getModelObject().isAutoJoin());
            }
        };

        protected CourseAccessPanel(String id, IModel<Course> courseIModel) {
            super(id, courseIModel);

            WebMarkupContainer accessControlContainer = new WebMarkupContainer("accessControl") {
                @Override
                public boolean isVisible() {
                    return controlAvailableModel.getObject();
                }
            };

            WebMarkupContainer noFullVersionContainer = new WebMarkupContainer("noFullVersion") {
                @Override
                public boolean isVisible() {
                    return !(controlAvailableModel.getObject());
                }
            };

            add(accessControlContainer);
            add(noFullVersionContainer);

            accessControlContainer.add(value.setOutputMarkupId(true));
            accessControlContainer.add(description.setOutputMarkupId(true));

            accessControlContainer.add(onButton.setOutputMarkupPlaceholderTag(true));
            accessControlContainer.add(offButton.setOutputMarkupPlaceholderTag(true));

            onButton.add(new AttributeModifier("title", new ResourceModel("onTooltip")));
            offButton.add(new AttributeModifier("title", new ResourceModel("offTooltip")));

            onButton.add(new TooltipBehavior());
            offButton.add(new TooltipBehavior());
        }

        private void changeStatus(boolean status) {
            Course course = CourseAccessPanel.this.getModelObject();
            course.setAutoJoin(status);
            try {
                courseManager.updateCourse(course);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot change access to course", e);
                throw new WicketRuntimeException("Cannot change access to course", e);
            }
            CourseAccessPanel.this.setModelObject(course);
        }
    }

}
