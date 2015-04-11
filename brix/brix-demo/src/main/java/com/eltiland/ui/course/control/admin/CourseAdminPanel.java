package com.eltiland.ui.course.control.admin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.course.ELTCourseAdminManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.CourseAdmin;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for controlling admins of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseAdminPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private ELTCourseManager courseManager;
    @SpringBean
    private ELTCourseAdminManager courseAdminManager;

    private ELTTable<User> adminTable;

    private ELTSelectDialog<User> selectDialog = new ELTSelectDialog<User>("selectDialog", 880) {
        @Override
        protected int getMaxRows() {
            return 20;
        }

        @Override
        protected String getHeader() {
            return getString("user.select");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            close(target);

            genericManager.initialize(CourseAdminPanel.this.getModelObject(),
                    CourseAdminPanel.this.getModelObject().getAdmins());
            genericManager.initialize(CourseAdminPanel.this.getModelObject(),
                    CourseAdminPanel.this.getModelObject().getAuthor());

            for (Long id : selectedIds) {
                CourseAdmin admin = new CourseAdmin();
                admin.setCourse(CourseAdminPanel.this.getModelObject());
                User user = genericManager.getObject(User.class, id);
                admin.setAdmin(user);
                try {
                    courseAdminManager.create(admin);
                    CourseAdminPanel.this.getModelObject().getAdmins().add(user);
                    courseManager.update(CourseAdminPanel.this.getModelObject());
                    target.add(adminTable);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("name.column"), "name", "name"));
            columns.add(new PropertyColumn<User>(new ResourceModel("email.column"), "email", "email"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return userManager.getUserSearchList(first, count,
                    getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return userManager.getUserSearchCount(getSearchString());
        }

        @Override
        protected String getSearchPlaceholder() {
            return getString("user.search");
        }
    };

    /**
     * Panel ctor
     *
     * @param id              markup id.
     * @param eltCourseIModel course model.
     */
    public CourseAdminPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        adminTable = new ELTTable<User>("grid", 30) {
            @Override
            protected List<IColumn<User>> getColumns() {
                List<IColumn<User>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<User>(new ResourceModel("name.column"), "name", "name"));
                columns.add(new PropertyColumn<User>(new ResourceModel("email.column"), "email", "email"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return userManager.getCourseAdmins(CourseAdminPanel.this.getModelObject(),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                genericManager.initialize(CourseAdminPanel.this.getModelObject(),
                        CourseAdminPanel.this.getModelObject().getAdmins());
                return CourseAdminPanel.this.getModelObject().getAdmins().size();
            }

            @Override
            protected String getNotFoundedMessage() {
                return CourseAdminPanel.this.getString("no.admins");
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<User> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.REMOVE));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ADD:
                        return getString("add.action");
                    case REMOVE:
                        return getString("remove.action");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected void onClick(IModel<User> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        selectDialog.show(target);
                        break;
                    case REMOVE:
                        ELTCourse course = CourseAdminPanel.this.getModelObject();
                        genericManager.initialize(course, course.getAdmins());
                        course.getAdmins().remove(rowModel.getObject());
                        try {
                            courseManager.update(course);
                            target.add(adminTable);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        add(adminTable.setOutputMarkupId(true));
        add(selectDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
