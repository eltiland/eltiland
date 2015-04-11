package com.eltiland.ui.webinars.components.multiply;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ProxyUtils;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for registration couple of users to webinar.
 *
 * @author Aleksey Plotnikov
 */
public class WebinarAddUsersPanel extends ELTDialogPanel implements IDialogSimpleUpdateCallback<List<User>> {
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private GenericManager genericManager;

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private IDialogActionProcessor<List<User>> callback;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private ELTSelectDialog<User> chooseUsersDialog = new ELTSelectDialog<User>(
            "chooseUsersDialog", 900) {

        @Override
        protected int getMaxRows() {
            return 10;
        }

        @Override
        protected String getHeader() {
            return getString("dialogHeader");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            for (Long id : selectedIds) {
                User user = userManager.getUserById(id);
                if (user == null) {
                    continue;
                }

                if (!usersModel.getObject().contains(user)) {
                    usersModel.getObject().add(user);
                }
            }

            close(target);
            target.add(chosenUsersTable);
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("userName"), "name", "name"));
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
            return null;
        }
    };

    private IModel<List<User>> usersModel = new ListModel<>(new ArrayList<User>());

    private ELTTable<User> chosenUsersTable = new ELTTable<User>("dataTable", 10) {
        @Override
        protected List<GridAction> getControlActions() {
            return new ArrayList<>(Arrays.asList(GridAction.ADD));
        }

        @Override
        protected boolean isControlling() {
            return true;
        }

        @Override
        protected List<GridAction> getGridActions(IModel<User> rowModel) {
            return new ArrayList<>(Arrays.asList(GridAction.REMOVE));
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            ArrayList<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(
                    new ResourceModel("userName"), "name", "name"));

            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return usersModel.getObject().subList(first, first + count).iterator();
        }

        @Override
        protected int getSize() {
            return usersModel.getObject().size();
        }

        @Override
        protected void onClick(IModel<User> rowModel, GridAction action, AjaxRequestTarget target) {
            switch (action) {
                case ADD:
                    chooseUsersDialog.show(target);
                    break;
                case REMOVE:
                    User needUser = rowModel.getObject();
                    int i = -1;
                    int foundedIndex = -1;
                    for (User item : usersModel.getObject()) {
                        i++;
                        if (item.getId().equals(needUser.getId())) {
                            foundedIndex = i;
                            break;
                        }
                    }
                    if (foundedIndex != -1) {
                        usersModel.getObject().remove(foundedIndex);
                    }
                    break;
            }
            target.add(chosenUsersTable);
        }
    };

    public WebinarAddUsersPanel(String id) {
        super(id);

        form.add(chosenUsersTable);
        form.add(chooseUsersDialog);
    }

    @Override
    protected String getHeader() {
        return getString("headerLabel");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Register));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Register)) {
            callback.process(usersModel, target);
        }
    }

    @Override
    public void setSimpleUpdateCallback(IDialogActionProcessor<List<User>> callback) {
        this.callback = callback;
    }

    public void initWebinarData(Webinar webinar) {
        webinarIModel.setObject(webinar);
        usersModel.getObject().clear();

        if (currentUserModel.getObject() == null) {
            return;
        }

        for (WebinarUserPayment userPayment : webinarUserPaymentManager.getWebinarPayments(webinar)) {
            genericManager.initialize(userPayment, userPayment.getUserProfile());
            if (userPayment.getUserProfile() != null) {
                usersModel.getObject().add(userPayment.getUserProfile());
            }
        }
    }
}
