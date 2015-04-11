package com.eltiland.ui.common.components.user_selector;

import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Panel for selecting webinar user for adding to webinar.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SelectUserPanel extends ELTDialogPanel implements IDialogSelectCallback<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectUserPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private ConfirmationManager confirmationManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;
    @SpringBean
    private EmailMessageManager emailMessageManager;


    protected static final int MAX_SEARCH_LEN = 1024;

    private IDialogActionProcessor<User> selectCallback;

    protected TextField<String> searchField = new TextField("searchField", new Model<String>());

    private final EltiDefaultDataGrid<UserDataSource, User> grid;

    private Dialog<RegisterUserPanel> registerDialog =
            new Dialog<RegisterUserPanel>("registerDialog", 320) {
                @Override
                public RegisterUserPanel createDialogPanel(String id) {
                    return new RegisterUserPanel(id);
                }

                @Override
                public void registerCallback(RegisterUserPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<User>() {
                        @Override
                        public void process(IModel<User> model, AjaxRequestTarget target) {
                            try {
                                User user = userManager.createUser(model.getObject());
                                user.setConfirmationDate(null);
                                genericManager.update(user);

                                Date endDate = DateTime.now().plusDays(Integer.decode(
                                        eltilandProps.getProperty("confirmation.days")).intValue()).toDate();
                                confirmationManager.createConfirmation(user, endDate);
                                emailMessageManager.sendEmailConfirmationToUser(user);
                            } catch (UserException | EltilandManagerException | ConstraintException e) {
                                LOGGER.error("Got exception when creating user", e);
                                throw new WicketRuntimeException("Got exception when creating user", e);
                            } catch (EmailException e) {
                                LOGGER.error("Got exception when sending email", e);
                                throw new WicketRuntimeException("Got exception when sending email", e);
                            }
                            ELTAlerts.renderOKPopup(getString("registerMessage"), target);
                            close(target);
                        }
                    });
                }
            };

    private EltiAjaxSubmitLink searchButton = new EltiAjaxSubmitLink("searchButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            target.add(grid);
        }
    };

    public SelectUserPanel(String id) {
        super(id);
        form.add(searchField);
        form.add(searchButton);

        List<IGridColumn<UserDataSource, User>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<UserDataSource, User, String>
                (new ResourceModel("fioColumn"), "name", "name"));
        columns.add(new PropertyColumn<UserDataSource, User, String>
                (new ResourceModel("emailColumn"), "email", "email"));
        columns.add(new AbstractColumn<UserDataSource, User>("action", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, final IModel<User> rowModel) {
                return new ActionPanel(componentId) {
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        String name = rowModel.getObject().getName();
                        String[] nameParts = name.split(" ");
                        if (nameParts.length != 3) {
                            ELTAlerts.renderErrorPopup(getString("errorInformatName"), target);
                        } else {
                            selectCallback.process(rowModel, target);
                        }
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new UserDataSource(), columns);
        grid.setOutputMarkupPlaceholderTag(true);
        form.add(grid);

        form.add(new EltiAjaxLink("registerLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                registerDialog.show(target);
            }
        });

        form.add(registerDialog);
    }

    @Override
    protected String getHeader() {
        return getString("userHeaderLabel");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public void setSelectCallback(IDialogActionProcessor<User> callback) {
        this.selectCallback = callback;
    }

    protected abstract void query(IDataSource.IQuery query, IDataSource.IQueryResult<User> result);

    private class UserDataSource implements IDataSource<User> {

        @Override
        public void query(IQuery query, IQueryResult<User> result) {
            SelectUserPanel.this.query(query, result);
        }

        @Override
        public IModel<User> model(User object) {
            return new GenericDBModel<>(User.class, object);
        }

        @Override
        public void detach() {
        }
    }

    private abstract class ActionPanel extends BaseEltilandPanel {
        public ActionPanel(String id) {
            super(id);

            add(new EltiAjaxLink("selectLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onSelect(target);
                }
            });
        }

        protected abstract void onSelect(AjaxRequestTarget target);
    }
}
