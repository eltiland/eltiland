package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.bl.*;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.EmailMessage;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.*;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.multiply.WebinarAddUsersPanel;
import com.eltiland.ui.webinars.plugin.components.WebinarModeratorialPanel;
import com.eltiland.ui.webinars.plugin.components.WebinarPropertyPanel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Webinars announcements management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WAnnouncementManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WAnnouncementManagementPanel.class);

    private ELTTable<Webinar> grid;

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private MailSender mailSender;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean(name = "mailMessageHeadings")
    Properties mailHeadings;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private void sendUpdateMessageToUsers(IModel<Webinar> model, AjaxRequestTarget target) {
        List<EmailMessage> messages = new ArrayList<>();
        Webinar webinar = model.getObject();
        genericManager.initialize(webinar, webinar.getWebinarUserPayments());
        try {
            for (WebinarUserPayment item : webinar.getWebinarUserPayments()) {
                EmailMessage emailMessage = new EmailMessage();
                String body = String.format(getString("updateWebinarMessage"), webinar.getName());
                emailMessage.setText(body);

                emailMessage.setSubject(getString("updateWebinarSubject"));

                InternetAddress recipient = new InternetAddress(item.getUserEmail());
                emailMessage.setRecipients(Arrays.asList(recipient));

                emailMessage.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));
                messages.add(emailMessage);
            }

            mailSender.sendMessages(messages.toArray(new EmailMessage[messages.size()]));
        } catch (UnsupportedEncodingException | AddressException | EmailException e) {
            ELTAlerts.renderErrorPopup(getString("sendMailsError"), target);
        }
    }

    private final Dialog<WebinarEditPanel> webinarPropertyPanelDialog =
            new Dialog<WebinarEditPanel>("editWebinarDialog", 350) {
                @Override
                public WebinarEditPanel createDialogPanel(String id) {
                    return new WebinarEditPanel(id);
                }

                @Override
                public void registerCallback(WebinarEditPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<Webinar>() {
                        @Override
                        public void process(IModel<Webinar> model, AjaxRequestTarget target) {
                            try {
                                webinarManager.update(model.getObject());
                            } catch (EltilandManagerException e) {
                                throw new WicketRuntimeException(e);
                            }

                            close(target);
                            sendUpdateMessageToUsers(model, target);
                            ELTAlerts.renderOKPopup(getString("messageEditSuccess"), target);
                            target.add(grid);
                        }
                    });
                }
            };

    private final Dialog<WebinarModeratorialPanel> webinarModeratorialPanelDialog =
            new Dialog<WebinarModeratorialPanel>("moderateWebinarDialog", 965) {
                @Override
                public WebinarModeratorialPanel createDialogPanel(String id) {
                    return new WebinarModeratorialPanel(id);
                }
            };

    private final Dialog<SendMessagePanel> sendMessagePanelDialog =
            new Dialog<SendMessagePanel>("sendMessageDialog", 750) {
                @Override
                public SendMessagePanel createDialogPanel(String id) {
                    return new SendMessagePanel(id);
                }

                @Override
                public void registerCallback(SendMessagePanel panel) {
                    panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("messageSend"), target);
                        }
                    });
                    super.registerCallback(panel);
                }
            };

    private Dialog<WebinarAddUsersPanel> webinarAddUsersPanelDialog =
            new Dialog<WebinarAddUsersPanel>("addCoupleUsersDialog", 420) {
                @Override
                public WebinarAddUsersPanel createDialogPanel(String id) {
                    return new WebinarAddUsersPanel(id);
                }

                @Override
                public void registerCallback(WebinarAddUsersPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<List<User>>() {
                        @Override
                        public void process(IModel<List<User>> model, AjaxRequestTarget target) {
                            if (!(model.getObject().isEmpty())) {
                                for (User user : model.getObject()) {
                                    if (webinarUserPaymentManager.hasAlreadyRegistered(
                                            webinarIModel.getObject(), user.getEmail())) {
                                        continue;
                                    }
                                    // split name into parts
                                    String name = user.getName();
                                    String[] nameParts = name.split(" ");
                                    String surnameValue = nameParts[0],
                                            nameValue = nameParts[1],
                                            patronymicValue = nameParts[2];

                                    WebinarUserPayment payment = new WebinarUserPayment();
                                    payment.setRole(WebinarUserPayment.Role.MEMBER);
                                    payment.setWebinar(webinarIModel.getObject());
                                    payment.setPrice(webinarIModel.getObject().getPrice());
                                    payment.setRegistrationDate(DateUtils.getCurrentDate());
                                    payment.setUserName(nameValue);
                                    payment.setPatronymic(patronymicValue);
                                    payment.setUserSurname(surnameValue);
                                    payment.setUserProfile(user);
                                    payment.setUserEmail(user.getEmail());

                                    try {
                                        webinarUserPaymentManager.createUser(payment);
                                    } catch (EltilandManagerException e) {
                                        LOGGER.error("Got exception when creating user", e);
                                        throw new WicketRuntimeException("Got exception when creating user", e);
                                    } catch (EmailException e) {
                                        LOGGER.error("Got exception when sending email", e);
                                        throw new WicketRuntimeException("Got exception when sending email", e);
                                    }

                                }
                                close(target);
                                ELTAlerts.renderOKPopup(getString("usersInvited"), target);
                            }
                        }
                    });
                }
            };


    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WAnnouncementManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<Webinar>("grid", 10) {
            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("editAction");
                    case USERS:
                        return getString("moderationAction");
                    case OFF:
                        return getString("closeRegAction");
                    case ON:
                        return getString("openRegAction");
                    case SEND:
                        return getString("sendAction");
                    default:
                        return "";
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Webinar> rowModel) {
                switch (action) {
                    case ON:
                        return rowModel.getObject().getStatus() == Webinar.Status.CLOSED;
                    case OFF:
                        return rowModel.getObject().getStatus() == Webinar.Status.OPENED;
                    default:
                        return true;
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Webinar> rowModel) {
                return Arrays.asList(GridAction.EDIT, GridAction.SEND, GridAction.USERS, GridAction.OFF, GridAction.ON);
            }

            @Override
            protected List<IColumn<Webinar>> getColumns() {
                List<IColumn<Webinar>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("descriptionLabel"), "description",
                        "description"));
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("startDateLabel"), "startDate", "startDate"));
                columns.add(new AbstractColumn<Webinar>(new ResourceModel("managerLabel")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Webinar>> components, String s,
                                             IModel<Webinar> webinarIModel) {
                        String name = String.format("%s %s", webinarIModel.getObject().getManagersurname(),
                                webinarIModel.getObject().getManagername());
                        components.add(new Label(s, new Model<>(name)));
                    }
                });
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("deadlineDateLabel"), "registrationDeadline",
                        "registrationDeadline"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarManager.getWebinarList(first, count, getSort().getProperty(), getSort().isAscending(),
                        true, true, null).iterator();
            }

            @Override
            protected int getSize() {
                return webinarManager.getWebinarCount(true, true, null);
            }

            @Override
            protected void onClick(IModel<Webinar> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case EDIT:
                        webinarPropertyPanelDialog.getDialogPanel().setWebinarModel(rowModel);
                        webinarPropertyPanelDialog.show(target);

                        break;

                    case USERS:
                        webinarIModel.setObject(rowModel.getObject());
                        // TODO: Данная панель не доделана. Возвращаю на старую
                        /*webinarAddUsersPanelDialog.getDialogPanel().initWebinarData(rowModel.getObject());
                        webinarAddUsersPanelDialog.show(target);*/
                        webinarModeratorialPanelDialog.getDialogPanel().initWebinarData(rowModel);
                        webinarModeratorialPanelDialog.show(target);

                        break;

                    case OFF:
                        try {
                            webinarManager.closeRegistration(rowModel.getObject());
                            ELTAlerts.renderOKPopup(getString("messageCloseWebinar"), target);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot close registration on webinar", e);
                            throw new WicketRuntimeException("Cannot close registration on webinar", e);
                        }
                        target.add(grid);

                        break;

                    case ON:
                        try {
                            webinarManager.openRegistration(rowModel.getObject());
                            ELTAlerts.renderOKPopup(getString("messageOpenWebinar"), target);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot open registration on webinar", e);
                            throw new WicketRuntimeException("Cannot open registration on webinar", e);
                        }
                        target.add(grid);

                        break;

                    case SEND:
                        sendMessagePanelDialog.getDialogPanel().initData(rowModel);
                        sendMessagePanelDialog.show(target);
                }
            }
        };

        add(grid);
        add(webinarPropertyPanelDialog);
        add(webinarModeratorialPanelDialog);
        add(webinarAddUsersPanelDialog);
        add(sendMessagePanelDialog);
        webinarModeratorialPanelDialog.setMinimalHeight(300);
    }

    private class WebinarEditPanel extends ELTDialogPanel implements IDialogSimpleUpdateCallback<Webinar> {
        private IDialogActionProcessor<Webinar> callback;
        private IModel<Webinar> model;

        private ELTTextField<String> topicField = new ELTTextField<>("topic",
                new ResourceModel("webinarTopicLabel"), new Model<String>(), String.class, true);
        private ELTTextArea descriptionField = new ELTTextArea("description",
                new ResourceModel("webinarDescriptionLabel"), new Model<String>(), true);
        private ELTTextArea shortDescriptionField = new ELTTextArea("shortDesc",
                new ResourceModel("webinarShortDescriptionLabel"), new Model<String>(), true);
        private ELTDateTimeField dateField = new ELTDateTimeField("date",
                new ResourceModel("webinarStartDate"), new Model<Date>(), Date.class, true);
        private PriceField priceField = new PriceField("price",
                new ResourceModel("webinarPriceLabel"), new Model<BigDecimal>());
        private ELTTextField<String> leaderNameField = new ELTTextField<>("leaderName",
                new ResourceModel("webinarLeadNameLabel"), new Model<String>(), String.class, true);
        private ELTTextField<String> leaderSurnameField = new ELTTextField<>("leaderSurname",
                new ResourceModel("webinarLeadSurnameLabel"), new Model<String>(), String.class, true);
        private ELTDateTimeField deadlineField = new ELTDateTimeField("deadline",
                new ResourceModel("webinarDeadLineLabel"), new Model<Date>(), Date.class);
        private ELTPasswordField passwordField = new ELTPasswordField("password",
                new ResourceModel("webinarPasswordLabel"), new Model<String>(), true);
        private ELTPasswordField passwordRetryField = new ELTPasswordField("passwordRetry",
                new ResourceModel("webinarPasswordRetryLabel"), new Model<String>(), true);
        private ELTTextField<Integer> durationField = new ELTTextField<>("duration",
                new ResourceModel("webinarDurationLabel"), new Model<Integer>(), Integer.class, true);
        private ELTAjaxCheckBox changePassword = new ELTAjaxCheckBox("changePassword",
                new ResourceModel("changePasswordLabel"), new Model<>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean value = getModelObject();

                passwordField.setEnabled(value);
                passwordRetryField.setEnabled(value);

                target.add(passwordField);
                target.add(passwordRetryField);
            }
        };
        private ELTAjaxCheckBox freeWebinarCheck = new ELTAjaxCheckBox(
                "freeWebinarCheck", new ResourceModel("freeWebinarLabel"), new Model<>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                refreshPriceField(!getModelObject());
                target.add(priceField);
            }
        };

        private void refreshPriceField(boolean enabled) {
            priceField.setEnabled(enabled);
            if (!enabled) {
                priceField.setValue(BigDecimal.ZERO);
            }
        }

        public WebinarEditPanel(String id) {
            super(id);

            form.add(topicField.setOutputMarkupId(true));
            form.add(descriptionField.setOutputMarkupId(true));
            form.add(shortDescriptionField.setOutputMarkupId(true));
            form.add(dateField.setOutputMarkupId(true));
            form.add(freeWebinarCheck);
            form.add(priceField.setOutputMarkupId(true));
            priceField.setRequired(true);
            form.add(leaderNameField.setOutputMarkupId(true));
            form.add(leaderSurnameField.setOutputMarkupId(true));
            form.add(deadlineField.setOutputMarkupId(true));
            form.add(changePassword);
            form.add(passwordField.setOutputMarkupId(true));
            form.add(passwordRetryField.setOutputMarkupId(true));
            form.add(durationField.setOutputMarkupId(true));

            form.add(new WebinarPropertyValidator());
            form.add(new FormRequired("required"));
        }

        public void setWebinarModel(IModel<Webinar> webinarIModel) {
            model = webinarIModel;

            Webinar webinar = webinarIModel.getObject();
            topicField.setModelObject(webinar.getName());
            descriptionField.setModelObject(webinar.getDescription());
            shortDescriptionField.setModelObject(webinar.getShortDesc());
            dateField.setModelObject(webinar.getStartDate());
            priceField.setValue(webinar.getPrice());
            leaderNameField.setModelObject(webinar.getManagername());
            leaderSurnameField.setModelObject(webinar.getManagersurname());
            deadlineField.setModelObject(webinar.getRegistrationDeadline());
            durationField.setModelObject(webinar.getDuration());
            passwordField.setModelObject("");
            passwordRetryField.setModelObject("");
            freeWebinarCheck.setModelObject(webinarIModel.getObject().getPrice() == null);
            refreshPriceField(!freeWebinarCheck.getModelObject());
            passwordField.setEnabled(false);
            passwordRetryField.setEnabled(false);
            changePassword.setModelObject(false);
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Save);
        }

        private void populateWebinar(Webinar webinar) {
            webinar.setName(StringUtils.replace(topicField.getModelObject(), "\"", ""));
            webinar.setDescription(descriptionField.getModelObject());
            webinar.setShortDesc(shortDescriptionField.getModelObject());
            webinar.setStartDate(dateField.getModelObject());
            webinar.setDuration(durationField.getModelObject());
            webinar.setPrice(priceField.getModelObject());
            webinar.setManagername(leaderNameField.getModelObject());
            webinar.setManagersurname(leaderSurnameField.getModelObject());
            webinar.setRegistrationDeadline(deadlineField.getModelObject());
            if (passwordField.isEnabled()) {
                webinar.setPassword(passwordField.getModelObject());
            }
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Save:
                    if (model == null) {
                        return;
                    }

                    Webinar webinar = model.getObject();
                    populateWebinar(webinar);

                    if (callback != null) {
                        callback.process(new GenericDBModel<>(Webinar.class, webinar), target);
                    }

                    break;
            }
        }

        @Override
        public void setSimpleUpdateCallback(IDialogActionProcessor<Webinar> callback) {
            this.callback = callback;
        }

        private class WebinarPropertyValidator extends AbstractFormValidator {
            @Override
            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{passwordField, passwordRetryField, dateField, deadlineField};
            }

            @Override
            public void validate(Form<?> components) {
                Date startDate = dateField.getConvertedInput();
                Date deadlineDate = deadlineField.getConvertedInput();

                if (passwordField.isEnabled() && passwordRetryField.isEnabled()) {
                    if (!(passwordField.getConvertedInput().equals(passwordRetryField.getConvertedInput()))) {
                        this.error(passwordField, "errorPasswordNotMatch");
                        this.error(passwordRetryField, "errorPasswordNotMatch");
                    }
                }

                if (startDate.before(DateUtils.getCurrentDate())) {
                    this.error(dateField, "errorStartDatePast");
                }

                if (deadlineDate.before(DateUtils.getCurrentDate())) {
                    this.error(deadlineField, "errorDeadlineDatePast");
                }

                if (deadlineDate.after(startDate)) {
                    this.error(dateField, "errorDeadlineAfterStart");
                    this.error(deadlineField, "errorDeadlineAfterStart");
                }
            }
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class SendMessagePanel extends ELTDialogPanel implements IDialogCloseCallback {

        private CKEditorFull contentField = new CKEditorFull("content", sendMessagePanelDialog);
        private ELTTextField<String> headerField = new ELTTextField<>(
                "messageHeader", new ResourceModel("headerMessage"), new Model<String>(), String.class);
        private IDialogActionProcessor callback;

        private IModel<Webinar> webinarIModel = new GenericDBModel<Webinar>(Webinar.class);

        public SendMessagePanel(String id) {
            super(id);

            form.add(contentField);
            form.add(headerField);
        }

        public void initData(IModel<Webinar> webinarIModel) {
            this.webinarIModel = webinarIModel;
        }

        @Override
        protected String getHeader() {
            return getString("headerLabel");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Collections.singletonList(EVENT.Send));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Send)) {
                try {
                    emailMessageManager.sendWebinarMessageToListeners(
                            webinarIModel.getObject(), contentField.getData(),
                            false, false, false, headerField.getModelObject());
                } catch (EmailException e) {
                    LOGGER.error("Cannot send message", e);
                    throw new WicketRuntimeException("Cannot send message", e);
                }
                callback.process(target);
            }
        }

        @Override
        public String getVariation() {
            return "styled";
        }

        @Override
        public void setCloseCallback(IDialogActionProcessor callback) {
            this.callback = callback;
        }
    }
}
