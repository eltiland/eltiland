package com.eltiland.ui.course.plugin.tab2;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
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
    private ELTCourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private ELTTable<ELTCourse> grid;

    public CourseInvoiceManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<ELTCourse>("grid", 15) {
            @Override
            protected List<IColumn<ELTCourse>> getColumns() {
                List<IColumn<ELTCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<ELTCourse>(new ResourceModel("dateLabel"), "creationDate", "creationDate"));
                columns.add(new PropertyColumn<ELTCourse>(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn<ELTCourse>(new ResourceModel("authorLabel"), "author.name", "author.name"));
                columns.add(new PriceColumn(new ResourceModel("priceLabel"), "price", "price"));
                columns.add(new AbstractColumn<ELTCourse>(new ResourceModel("infoLabel")) {
                    @Override
                    public void populateItem(
                            Item<ICellPopulator<ELTCourse>> components, String s, IModel<ELTCourse> courseIModel) {
                        components.add(new InfoPanel(s, courseIModel));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getCourseList(new ArrayList<>(Arrays.asList(CourseStatus.NEW)),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getCourseListCount(new ArrayList<>(Arrays.asList(CourseStatus.NEW)));
            }

            @Override
            protected void onClick(IModel<ELTCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.APPLY)) {
                    ELTCourse course = rowModel.getObject();
                    course.setStatus(CourseStatus.CONFIRMED);

                    try {
                        GoogleDriveFile file = googleDriveManager.createEmptyDoc(
                                "Cтартовая страница курса \"" + course.getName() + "\"", GoogleDriveFile.TYPE.DOCUMENT);
                        course.setStartPage(file);
                    } catch (GoogleDriveException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    try {
                        courseManager.update(course);
                        ELTAlerts.renderOKPopup(getString("applyActionMessage"), target);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    try {
                        genericManager.initialize(course, course.getAuthor());
                        emailMessageManager.sendEmailApplyCourseToUser(course);
                    } catch (EmailException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    target.add(grid);
                } else if (action.equals(GridAction.REMOVE)) {
                    ELTCourse course = rowModel.getObject();

                    try {
                        genericManager.initialize(course, course.getAuthor());
                        emailMessageManager.sendEmailDenyCourseToUser(course);
                    } catch (EmailException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    try {
                        courseManager.delete(course);
                        ELTAlerts.renderOKPopup(getString("deleteActionMessage"), target);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    target.add(grid);
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourse> rowModel) {
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

    private class InfoPanel extends BaseEltilandPanel<ELTCourse> {

        protected InfoPanel(String id, IModel<ELTCourse> courseIModel) {
            super(id, courseIModel);

            add(new Label("kind", getString(
                    courseIModel.getObject() instanceof TrainingCourse ? "trainingCourse" : "authorCourse")));

            WebMarkupContainer trainingContainer = new WebMarkupContainer("trainingContainer") {
                @Override
                public boolean isVisible() {
                    return getModelObject() instanceof TrainingCourse;
                }
            };
            add(trainingContainer);

            if (getModelObject() instanceof TrainingCourse) {
                trainingContainer.add(new Label("prejoin", String.format(getString("prejoinDate"),
                        DateUtils.formatDate(((TrainingCourse) getModelObject()).getJoinDate()))));
                trainingContainer.add(new Label("session", String.format(getString("courseDates"),
                        DateUtils.formatDate(((TrainingCourse) getModelObject()).getStartDate()),
                        DateUtils.formatDate(((TrainingCourse) getModelObject()).getFinishDate()))));
            }
        }
    }
}
