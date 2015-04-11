package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CourseManager;
import com.eltiland.bl.CourseSessionManager;
import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Courses invoice management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CourseInvoiceManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private CourseSessionManager courseSessionManager;

    private final Logger LOGGER = LoggerFactory.getLogger(CourseInvoiceManagementPanel.class);

    private ELTTable<Course> grid;

    public CourseInvoiceManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<Course>("grid", 15) {
            @Override
            protected List<IColumn<Course>> getColumns() {
                List<IColumn<Course>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Course>(new ResourceModel("dateLabel"), "creationDate", "creationDate"));
                columns.add(new PropertyColumn<Course>(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn<Course>(new ResourceModel("authorLabel"), "author.name", "author.name"));
                columns.add(new PriceColumn(new ResourceModel("priceLabel"), "price", "price"));
                columns.add(new AbstractColumn<Course>(new ResourceModel("infoLabel")) {
                    @Override
                    public void populateItem(
                            Item<ICellPopulator<Course>> components, String s, IModel<Course> courseIModel) {
                        components.add(new InfoPanel(s, courseIModel));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getCourseList(
                        false, first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getCourseListCount(false);
            }

            @Override
            protected void onClick(IModel<Course> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.APPLY)) {
                    Course course = rowModel.getObject();
                    course.setStatus(true);

                    try {
                        GoogleDriveFile file = googleDriveManager.createEmptyDoc(
                                "Cтартовая страница курса \"" + course.getName() + "\"", GoogleDriveFile.TYPE.DOCUMENT);
                        course.setStartPage(file);
                    } catch (GoogleDriveException e) {
                        LOGGER.error("Cannot create start Google page", e);
                        throw new WicketRuntimeException("Cannot create start Google page", e);
                    }

                    try {
                        courseManager.updateCourse(course);
                        ELTAlerts.renderOKPopup(getString("applyActionMessage"), target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot apply course", e);
                        throw new WicketRuntimeException("Cannot apply course", e);
                    }
//                    try {
//                        genericManager.initialize(course, course.getAuthor());
//                        emailMessageManager.sendEmailApplyCourseToUser(course);
//                    } catch (EmailException e) {
//                        LOGGER.error("Cannot send email about applying course", e);
//                        throw new WicketRuntimeException("Cannot send email about applying course", e);
//                    }
                    target.add(grid);
                } else if (action.equals(GridAction.REMOVE)) {
                    Course course = rowModel.getObject();

//                    try {
//                        genericManager.initialize(course, course.getAuthor());
//                        emailMessageManager.sendEmailDenyCourseToUser(course);
//                    } catch (EmailException e) {
//                        LOGGER.error("Cannot send email about applying course", e);
//                        throw new WicketRuntimeException("Cannot send email about applying course", e);
//                    }

                    try {
                        courseManager.deleteCourse(course);
                        ELTAlerts.renderOKPopup(getString("deleteActionMessage"), target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot delete course", e);
                        throw new WicketRuntimeException("Cannot delete course", e);
                    }

                    target.add(grid);
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Course> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.APPLY, GridAction.REMOVE));
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.APPLY)) {
                    return getString("applyTooltip");
                } else if (action.equals(GridAction.REMOVE)) {
                    return getString("removeTooltip");
                } else {
                    return StringUtils.EMPTY;
                }
            }
        };
        add(grid.setOutputMarkupId(true));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class InfoPanel extends BaseEltilandPanel<Course> {

        protected InfoPanel(String id, IModel<Course> courseIModel) {
            super(id, courseIModel);

            add(new Label("kind", getString(
                    courseIModel.getObject().isTraining() ? "trainingCourse" : "authorCourse")));

            WebMarkupContainer trainingContainer = new WebMarkupContainer("trainingContainer") {
                @Override
                public boolean isVisible() {
                    return getModelObject().isTraining();
                }
            };
            add(trainingContainer);

            if (getModelObject().isTraining()) {
                CourseSession session = courseSessionManager.getActiveSession(getModelObject());

                trainingContainer.add(new Label("prejoin", String.format(
                        getString("prejoinDate"), DateUtils.formatDate(session.getPrejoinDate()))));
                trainingContainer.add(new Label("session", String.format(getString("courseDates"),
                        DateUtils.formatDate(session.getStartDate()),
                        DateUtils.formatDate(session.getFinishDate()))));
            }
        }
    }
}
