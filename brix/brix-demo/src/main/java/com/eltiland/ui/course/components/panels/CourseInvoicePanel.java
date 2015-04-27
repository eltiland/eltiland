package com.eltiland.ui.course.components.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseUserDataManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.*;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.ajaxradio.AjaxRadioPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for creating invoice for the access to the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseInvoicePanel extends ELTDialogPanel implements IDialogNewCallback<ELTCourseListener> {

    @SpringBean
    private ELTCourseUserDataManager courseUserDataManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;

    private IDialogActionProcessor<ELTCourseListener> callback;

    private IModel<ELTCourse> courseIModel = new GenericDBModel<>(ELTCourse.class);

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private ELTTable<User> childTable;

    private IModel<List<Long>> idsModel = new ListModel<>(new ArrayList<Long>());
    private int first, count;

    private IModel<List<User>> childListModel = new LoadableDetachableModel<List<User>>() {
        @Override
        protected List<User> load() {
            List<User> userList = new ArrayList<>();
            int size = idsModel.getObject().size();

            int limit = (first + count > size) ? size : (first + count);
            for (int i = first; i < limit; i++) {
                userList.add(genericManager.getObject(User.class, idsModel.getObject().get(i)));
            }
            return userList;
        }
    };

    ListenerType kind = ListenerType.PHYSICAL;

    private ELTTextField<String> nameField = new ELTTextField<String>(
            "nameField", new ResourceModel("name"), new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };
    private ELTTextField<String> companyField = new ELTTextField<String>(
            "organizationField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };
    private ELTTextField<String> positionField = new ELTTextField<String>(
            "positionField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };
    private ELTTextField<String> phoneField = new ELTTextField<String>(
            "phoneField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };
    private ELTTextField<Integer> experienceField = new ELTTextField<Integer>(
            "experienceField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<Integer>(), Integer.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };
    private ELTTextField<String> addressField = new ELTTextField<String>(
            "addressField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 375;
        }
    };

    private WebMarkupContainer legalContainer = new WebMarkupContainer("legalContainer");

    private ELTSelectDialog<User> userSelectDialog = new ELTSelectDialog<User>("userSelectorDialog", 890) {
        @Override
        protected int getMaxRows() {
            return 20;
        }

        @Override
        protected String getHeader() {
            return getString("user.selector");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            close(target);
            idsModel.setObject(selectedIds);
            childListModel.detach();
            target.add(childTable);
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("name.column"), "name", "name"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return userManager.getUserSearchList(
                    first, count, getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return userManager.getUserSearchCount(getSearchString());
        }
    };

    private AjaxRadioPanel<ValueMap> selector = new AjaxRadioPanel<ValueMap>(
            "selector", getValues(), getValues().get(0), "name") {
        @Override
        protected void onRadioSelect(AjaxRequestTarget target, ValueMap newSelection) {
            if (newSelection.equals(getValues().get(0))) {
                kind = ListenerType.PHYSICAL;
                legalContainer.setVisible(false);
            } else if (newSelection.equals(getValues().get(1))) {
                kind = ListenerType.LEGAL;
                legalContainer.setVisible(true);
            } else if (newSelection.equals(getValues().get(2))) {
                kind = ListenerType.MOSCOW;
                legalContainer.setVisible(true);
            }
            target.add(legalContainer);
        }
    };

    public CourseInvoicePanel(String id, IModel<ELTCourse> courseIModel) {
        super(id);
        this.courseIModel = courseIModel;

        form.add(nameField);
        nameField.setReadonly(true);

        constructField(companyField, UserDataType.COMPANY);
        constructField(positionField, UserDataType.JOB);
        constructField(phoneField, UserDataType.PHONE);
        constructField(experienceField, UserDataType.EXPERIENCE);
        constructField(addressField, UserDataType.ADDRESS);
        form.add(selector);
        selector.setVisible(courseIModel.getObject() instanceof TrainingCourse);
        form.add(companyField);
        form.add(positionField);
        form.add(phoneField);
        form.add(experienceField);
        form.add(addressField);
        form.add(legalContainer.setOutputMarkupPlaceholderTag(true));
        childTable = new ELTTable<User>("childTable", 10) {
            @Override
            protected List<IColumn<User>> getColumns() {
                List<IColumn<User>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<User>(new ResourceModel("name.column"), "name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                CourseInvoicePanel.this.first = first;
                CourseInvoicePanel.this.count = count;
                childListModel.detach();
                return childListModel.getObject().iterator();
            }

            @Override
            protected int getSize() {
                return idsModel.getObject().size();
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.EDIT));
            }

            @Override
            protected void onClick(IModel<User> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.EDIT)) {
                    userSelectDialog.getDialogPanel().setSelectedIds(idsModel.getObject());
                    userSelectDialog.show(target);
                }
            }

            @Override
            protected String getNotFoundedMessage() {
                return CourseInvoicePanel.this.getString("no.users");
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.EDIT)) {
                    return getString("edit.tooltip");
                } else {
                    return StringUtils.EMPTY;
                }
            }
        };
        legalContainer.add(childTable.setOutputMarkupId(true));
        legalContainer.setVisible(false);

        phoneField.setModelObject(currentUserModel.getObject().getPhone());
        positionField.setModelObject(currentUserModel.getObject().getAppointment());
        nameField.setModelObject(currentUserModel.getObject().getName());
        companyField.setModelObject(currentUserModel.getObject().getOrganization());
        experienceField.setModelObject(currentUserModel.getObject().getExperience());
        addressField.setModelObject(currentUserModel.getObject().getAddress());

        form.add(userSelectDialog);
        form.setMultiPart(true);
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Send));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Send)) {
            boolean toUpdate = false;
            if (positionField.isVisible()) {
                String appointment = currentUserModel.getObject().getAppointment();
                String newAppoitment = positionField.getModelObject();
                if (willBeUpdated(appointment, newAppoitment)) {
                    currentUserModel.getObject().setAppointment(newAppoitment);
                    toUpdate = true;
                }
            }

            if (phoneField.isVisible()) {
                String phone = currentUserModel.getObject().getPhone();
                String newPhone = phoneField.getModelObject();
                if (willBeUpdated(phone, newPhone)) {
                    currentUserModel.getObject().setPhone(newPhone);
                    toUpdate = true;
                }
            }

            if (companyField.isVisible()) {
                String organization = currentUserModel.getObject().getOrganization();
                String newOrganization = companyField.getModelObject();
                if (willBeUpdated(organization, newOrganization)) {
                    currentUserModel.getObject().setPhone(newOrganization);
                    toUpdate = true;
                }
            }

            if (addressField.isVisible()) {
                String address = currentUserModel.getObject().getAddress();
                String newAddress = addressField.getModelObject();
                if (willBeUpdated(address, newAddress)) {
                    currentUserModel.getObject().setAddress(newAddress);
                    toUpdate = true;
                }
            }

            if (experienceField.isVisible()) {
                Integer exp = currentUserModel.getObject().getExperience();
                Integer newExp = experienceField.getModelObject();
                if ((exp == null) ? (newExp != null) : (!exp.equals(newExp))) {
                    currentUserModel.getObject().setExperience(newExp);
                    toUpdate = true;
                }
            }

            if (toUpdate) {
                try {
                    userManager.updateUser(currentUserModel.getObject());
                } catch (UserException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }

            ELTCourseListener listener = new ELTCourseListener();
            listener.setType(kind);
            listener.setStatus(PaidStatus.NEW);
            listener.setListener(currentUserModel.getObject());
            listener.setCourse(courseIModel.getObject());
            try {
                courseListenerManager.create(listener);
                genericManager.initialize(listener, listener.getListeners());
                if (!(kind.equals(ListenerType.PHYSICAL))) {
                    for (Long id : idsModel.getObject()) {
                        User user = genericManager.getObject(User.class, id);
                        ELTCourseListener child = new ELTCourseListener();
                        child.setType(ListenerType.PHYSICAL);
                        child.setCourse(courseIModel.getObject());
                        child.setListener(user);
                        child.setStatus(PaidStatus.NEW);
                        child.setParent(listener);
                        listener.getListeners().add(child);

                        try {
                            courseListenerManager.create(child);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }
                }
                ELTAlerts.renderOKPopup(getString("invoiceApproved"), target);
            } catch (CourseException e) {
                ELTAlerts.renderErrorPopup(e.getMessage(), target);
            }

            callback.process(new GenericDBModel<>(ELTCourseListener.class, listener), target);
        }
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    private <T extends Serializable> void constructField(ELTTextField<T> textField, UserDataType type) {
        final ELTCourseUserData data = courseUserDataManager.get(courseIModel.getObject(), type);
        UserDataStatus status = data.getStatus();
        boolean isActive = !(status.equals(UserDataStatus.NO));
        textField.setVisible(isActive);
        if (isActive) {
            textField.setHeaderLabel(new Model<>(data.getCaption()));
        }
        textField.setValueRequired(status.equals(UserDataStatus.REQUIRED));
    }

    private boolean willBeUpdated(String oldStr, String newStr) {
        if (oldStr == null) {
            return (newStr != null && newStr.isEmpty());
        } else {
            return !(oldStr.equals(newStr));
        }
    }

    private List<ValueMap> getValues() {
        List<ValueMap> values = new ArrayList<>();
        values.add(newValue(getString("physical")));
        values.add(newValue(getString("legal")));
        values.add(newValue(getString("legal_portal")));
        return values;
    }

    private ValueMap newValue(String valueName) {
        ValueMap map = new ValueMap();
        map.put("name", valueName);

        return map;
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<ELTCourseListener> callback) {
        this.callback = callback;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
