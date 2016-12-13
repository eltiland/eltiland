package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.*;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Webinar creating/editing panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarPropertyPanel extends BaseEltilandPanel<Webinar> implements IDialogUpdateCallback<Webinar> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebinarPropertyPanel.class);

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private Form form = new Form("form");

    private static final int MAX_LEN = 1024;
    private static final int SHORT_LEN = 255;

    /**
     * Panel constructor (create mode).
     *
     * @param id panel's ID.
     */
    public WebinarPropertyPanel(String id) {
        super(id);
        addComponents();
    }

    /**
     * Panel constructor (edit mode).
     *
     * @param id            panel's ID.
     * @param webinarIModel webinar model.
     */
    public WebinarPropertyPanel(String id, IModel<Webinar> webinarIModel) {
        super(id, webinarIModel);
        addComponents();
    }

    public void setWebinarModel(IModel<Webinar> webinarIModel) {
        setModel(webinarIModel);

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

        headerContainer.setVisible(true);
        passwordField.setVisible(false);
        passwordRetryField.setVisible(false);
        leaderNameField.setVisible(false);
        leaderEmailField.setVisible(false);
        leaderSurnameField.setVisible(false);

        createButton.setVisible(false);
        saveButton.setVisible(true);
    }

    private IDialogUpdateCallback.IDialogActionProcessor<Webinar> updateCallback;

    private WebMarkupContainer headerContainer = new WebMarkupContainer("headerContainer");
    private ELTTextField topicField = new ELTTextField("topic",
            new ResourceModel("webinarTopicLabel"), new Model<String>(), String.class, true);
    private ELTTextArea descriptionField = new ELTTextArea("description",
            new ResourceModel("webinarDescriptionLabel"), new Model<String>(), true);
    private ELTTextArea shortDescriptionField = new ELTTextArea("shortDesc",
            new ResourceModel("webinarShortDescriptionLabel"), new Model<String>(), true);
    private ELTDateTimeField dateField = new ELTDateTimeField("date",
            new ResourceModel("webinarStartDate"), new Model<Date>(), Date.class, true);
    private PriceField priceField = new PriceField("price",
            new ResourceModel("webinarPriceLabel"), new Model<BigDecimal>());
    private ELTTextField leaderNameField = new ELTTextField<>("leaderName",
            new ResourceModel("webinarLeadNameLabel"), new Model<String>(), String.class, true);
    private ELTTextField leaderSurnameField = new ELTTextField("leaderSurname",
            new ResourceModel("webinarLeadSurnameLabel"), new Model<String>(), String.class, true);
    private ELTTextEmailField leaderEmailField = new ELTTextEmailField("leaderEmail",
            new ResourceModel("webinarLeadEmailLabel"), new Model<String>(), true);
    private ELTDateTimeField deadlineField = new ELTDateTimeField("deadline",
            new ResourceModel("webinarDeadLineLabel"), new Model<Date>(), Date.class);
    private ELTPasswordField passwordField = new ELTPasswordField("password",
            new ResourceModel("webinarPasswordLabel"), new Model<String>(), true);
    private ELTPasswordField passwordRetryField = new ELTPasswordField("passwordRetry",
            new ResourceModel("webinarPasswordRetryLabel"), new Model<String>(), true);
    private ELTTextField<Integer> durationField = new ELTTextField<>("duration",
            new ResourceModel("webinarDurationLabel"), new Model<Integer>(), Integer.class, true);
    private ELTAjaxCheckBox freeWebinarCheck = new ELTAjaxCheckBox(
            "freeWebinarCheck", new ResourceModel("freeWebinarLabel"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            boolean value = getModelObject();
            priceField.setEnabled(!value);
            if (getModelObject()) {
                priceField.setValue(BigDecimal.ZERO);
            }
            target.add(priceField);
        }
    };

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            Webinar webinar = new Webinar();
            populateWebinar(webinar);
            webinar.setStatus(Webinar.Status.OPENED);
            webinar.setPassword(passwordField.getModelObject());

            createWebinar(webinar, target);

            clearPanel(target);
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            Webinar webinar = (Webinar) WebinarPropertyPanel.this.getDefaultModelObject();
            populateWebinar(webinar);
            try {
                webinarManager.update(webinar);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot update webinar.", e);
                throw new WicketRuntimeException("Cannot update webinar.", e);
            }

            if (updateCallback != null) {
                updateCallback.process(new GenericDBModel<>(Webinar.class, webinar), target);
            }
        }
    };

    private ELTAjaxCheckBox confirmField =
            new ELTAjaxCheckBox("confirm", new ResourceModel("label.confirmation"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {

        }
    };

    private void addComponents() {
        add(headerContainer);
        headerContainer.setVisible(false);
        add(form.setOutputMarkupId(true));
        form.add(topicField.setOutputMarkupId(true));
        form.add(descriptionField.setOutputMarkupId(true));
        form.add(shortDescriptionField.setOutputMarkupId(true));
        form.add(dateField.setOutputMarkupId(true));
        form.add(freeWebinarCheck);
        form.add(priceField.setOutputMarkupId(true));
        priceField.setRequired(true);
        form.add(leaderNameField.setOutputMarkupId(true));
        form.add(leaderEmailField.setOutputMarkupId(true));
        form.add(leaderSurnameField.setOutputMarkupId(true));
        form.add(deadlineField.setOutputMarkupId(true));
        form.add(passwordField);
        form.add(passwordRetryField);
        form.add(durationField.setOutputMarkupId(true));
        form.add(createButton);
        form.add(saveButton.setVisible(false));
        form.add(confirmField);

        form.add(new WebinarPropertyValidator());

        form.add(new FormRequired("required"));
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<Webinar> callback) {
        this.updateCallback = callback;
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

            if (!(passwordField.getConvertedInput().equals(passwordRetryField.getConvertedInput()))) {
                this.error(passwordField, "errorPasswordNotMatch");
                this.error(passwordRetryField, "errorPasswordNotMatch");
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

    private void clearPanel(AjaxRequestTarget target) {
        topicField.setModelObject(null);
        topicField.addMaxLengthValidator(SHORT_LEN);
        descriptionField.setModelObject(null);
        descriptionField.addMaxLengthValidator(MAX_LEN);
        shortDescriptionField.setModelObject(null);
        shortDescriptionField.addMaxLengthValidator(MAX_LEN);
        dateField.setModelObject(null);
        durationField.setModelObject(null);
        priceField.setValue(null);
        leaderNameField.setModelObject(null);
        leaderSurnameField.setModelObject(null);
        leaderEmailField.setModelObject(null);
        deadlineField.setModelObject(null);
        passwordField.setModelObject(null);
        passwordRetryField.setModelObject(null);
        leaderNameField.addMaxLengthValidator(SHORT_LEN);
        leaderSurnameField.addMaxLengthValidator(SHORT_LEN);

        target.add(topicField);
        target.add(descriptionField);
        target.add(durationField);
        target.add(leaderNameField);
        target.add(form);
    }

    /**
     * Populate webinar object from form inputs.
     *
     * @param webinar to populate
     */
    private void populateWebinar(Webinar webinar) {
        webinar.setName(StringUtils.replace((String) topicField.getModelObject(), "\"", ""));
        webinar.setDescription(descriptionField.getModelObject());
        webinar.setShortDesc(shortDescriptionField.getModelObject());
        webinar.setStartDate(dateField.getModelObject());
        webinar.setDuration(durationField.getModelObject());
        webinar.setPrice(priceField.getModelObject());
        webinar.setManagername((String) leaderNameField.getModelObject());
        webinar.setManagersurname((String) leaderSurnameField.getModelObject());
        webinar.setRegistrationDeadline(deadlineField.getModelObject());
        webinar.setNeedConfirm(confirmField.getModelObject());
    }

    /**
     * Populate webinar user object from form inputs.
     *
     * @param webinar user to populate
     */
    protected void populateWebinarModerator(WebinarUserPayment user, Webinar webinar) {
        user.setWebinar(webinar);
        user.setUserEmail(leaderEmailField.getModelObject());
        user.setUserName((String) leaderNameField.getModelObject());
        user.setUserSurname((String) leaderSurnameField.getModelObject());
        user.setRole(WebinarUserPayment.Role.MODERATOR);
        user.setStatus(PaidStatus.CONFIRMED);
        user.setRegistrationDate(DateUtils.getCurrentDate());
    }

    protected void createWebinar(Webinar webinar, AjaxRequestTarget target) {
        webinar.setApproved(true);
        //webinar.setCourse(false);
        try {
            webinarManager.create(webinar);
        } catch (EltilandManagerException e) {
            LOGGER.error("Cannot create webinar.", e);
            throw new WicketRuntimeException("Cannot create webinar.", e);
        } catch (WebinarException e) {
            e.printStackTrace();
        }

        WebinarUserPayment userPayment = new WebinarUserPayment();
        populateWebinarModerator(userPayment, webinar);

        try {
            webinarUserPaymentManager.createModerator(userPayment);
        } catch (EltilandManagerException e) {
            LOGGER.error("Cannot create moderator of the webinar.", e);
            throw new WicketRuntimeException("Cannot create moderator of the webinar.", e);
        } catch (WebinarException e) {
            e.printStackTrace();
        }

        try {
            emailMessageManager.sendWebinarApplyManager(webinar, userPayment.getUserEmail());
            emailMessageManager.sendWebinarCreateToAdmin(webinar);
        } catch (EmailException e) {
            LOGGER.error("Cannot send email to manager", e);
            throw new WicketRuntimeException("Cannot send email to manager.", e);
        }
        ELTAlerts.renderOKPopup(getString("messageWebinarCreated"), target);
    }
}
