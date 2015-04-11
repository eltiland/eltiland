package com.eltiland.ui.subscribe;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.subscribe.SubscribeRequestData;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.components.label.EditableLabel;
import com.eltiland.ui.common.components.label.EmailEditableLabel;
import com.eltiland.ui.common.components.select.ELTSelectField;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Panel for process user request for magazine subscribe.
 *
 * @author Igor Cherednichenko
 */
public class RequestMagazineSubscribePanel extends BaseEltilandPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagazineSubscribePanel.class);
    public static final String GROUP_PEI = "ДОУ";
    public static final String GROUP_COMMITTEE = "Родительский комитет";
    public static final String GROUP_INDIVIDUAL = "Индивидуальный";

    @SpringBean
    private EmailMessageManager emailMessageManager;

    @SpringBean(name = "eltilandProperties")
    Properties eltilandProps;
    private Model<Boolean> groupTypeModel;
    private Model<String> orderNumberModel = new Model<String>();


    private ELTSelectField<String> groupField = new ELTSelectField<String>("groupField",
            new ResourceModel("groupField"), new Model<String>(), true) {
        @Override
        protected IModel<List<String>> getChoiceListModel() {
            return new LoadableDetachableModel<List<String>>() {
                @Override
                protected List<String> load() {
                    return new ArrayList<>(Arrays.asList(GROUP_PEI, GROUP_COMMITTEE));
                }
            };
        }

        @Override
        protected IChoiceRenderer<String> getChoiceRenderer() {
            return new IChoiceRenderer<String>() {
                @Override
                public Object getDisplayValue(String object) {

                    return object;
                }

                @Override
                public String getIdValue(String object, int index) {
                    return object;
                }
            };
        }
    };
    private TextArea<String> offerTextField = new TextArea<String>("offerTextField", new ResourceModel("offerText")) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setEnabled(false);
            setVisible(!groupTypeModel.getObject());
        }
    };
    private CheckBox offerApproveCheck = new CheckBox("offerApproveCheck", new Model<Boolean>(false)) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!groupTypeModel.getObject());
        }
    };
    private ELTFeedbackLabel offerApproveLabel =
            new ELTFeedbackLabel(
                    "offerApproveLabel",
                    new ResourceModel("offerApproveLabel"),
                    offerApproveCheck
            ) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(!groupTypeModel.getObject());
                }
            };
    /*
   Subscriber components
    */
    private EditableLabel<String> peiNameField =
            new EditableLabel<String>("peiName", new ResourceModel("peiNameTitle"), new Model<String>()) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(groupTypeModel.getObject());
                }
            };

    private EditableLabel<String> peiAddressField =
            new EditableLabel<String>("peiAddress", new Model<String>(), new Model<String>()) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    if (groupTypeModel.getObject()) {
                        setHeaderModel(new ResourceModel("peiAddressTitle"));
                    } else {
                        setHeaderModel(new ResourceModel("addressTitle"));
                    }
                }
            };

    private EmailEditableLabel peiEmailField =
            new EmailEditableLabel("peiEmail", new ResourceModel("peiEmailTitle"), new Model<String>());

    private EditableLabel<String> peiPrincipalField =
            new EditableLabel<String>("peiPrincipal", new ResourceModel("peiPrincipalTitle"), new Model<String>());

    private EditableLabel<String> peiTelephoneField =
            new EditableLabel<String>("peiTelephone", new ResourceModel("peiTelephoneTitle"), new Model<String>());

    private EditableLabel<Integer> magazineCountField =
            new EditableLabel<Integer>(
                    "magazineCount",
                    new ResourceModel("magazineCountTitle"),
                    new Model<Integer>(),
                    Integer.class) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    add(new MinimumValidator<Integer>(
                            Integer.decode(eltilandProps.getProperty("magazine.subscribe.minimumCount"))));
                    setVisible(groupTypeModel.getObject());
                }
            };
    private Label offerLabel = new Label("offerLabel", new ResourceModel("offerLabel")) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!groupTypeModel.getObject());
        }
    };
    /*
    
    Common components
     */
    private Form<Void> form = new Form<Void>("form") {
        @Override
        protected void onInitialize() {
            super.onInitialize();
            setVisible(true);
        }
    };
    private EltiAjaxSubmitLink submitButton = new EltiAjaxSubmitLink("submitButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {

                SubscribeRequestData subscribeRequestData = new SubscribeRequestData();
                subscribeRequestData.setPeiName(peiNameField.getModelObject());
                subscribeRequestData.setPeiAddress(peiAddressField.getModelObject());
                subscribeRequestData.setPeiEmail(peiEmailField.getModelObject());
                subscribeRequestData.setPeiPrincipal(peiPrincipalField.getModelObject());
                subscribeRequestData.setPeiTelephone(peiTelephoneField.getModelObject());

                subscribeRequestData.setOrderNumber(getOrderNumber());
                if (groupTypeModel.getObject()) {
                    subscribeRequestData.setMagazineCount(magazineCountField.getModelObject());
                    subscribeRequestData.setGroupType(groupField.getModelObject());
                } else {
                    subscribeRequestData.setGroupType(GROUP_INDIVIDUAL);
                    subscribeRequestData.setMagazineCount(
                            Integer.decode(eltilandProps.getProperty("magazine.subscribe.individualCount")));

                }


               // emailMessageManager.sendEmailToAdminAndAnonimousRequestSubscribe(subscribeRequestData);
                ELTAlerts.renderOKPopup(getString("requestWasSentSuccessfully"), target);

                form.setVisible(false);
                requestSentLabel.setVisible(true);
                target.add(formContainer);
        }
    };
    private Label requestSentLabel = new Label("requestSent", new ResourceModel("requestSentLabel")) {
        @Override
        protected void onInitialize() {
            super.onInitialize();
            setVisible(false);
        }
    };
    private Label subscribeSpecialRequirements =
            new Label("subscribeSpecialRequirements",
                    new StringResourceModel("subscribeSpecialRequirements", this, null,
                            new String[]{eltilandProps.getProperty("magazine.subscribe.minimumCount")})) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(groupTypeModel.getObject());
                }
            };
    private WebMarkupContainer formContainer = new WebMarkupContainer("formContainer");


    public RequestMagazineSubscribePanel(String id, Model model) {
        super(id);
        groupTypeModel = model;
        form.add(new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent<?>[]{offerApproveCheck};
            }

            @Override
            public void validate(Form<?> form) {
                if (!offerApproveCheck.getConvertedInput()) {
                    this.error(offerApproveCheck, "offerApproveError");
                }
            }
        });
        form.add(groupField.setRequired(true).setOutputMarkupId(true));
        form.add(offerLabel);
        form.add(offerTextField);
        form.add(offerApproveCheck.setRequired(true));
        form.add(offerApproveLabel);
        form.add(peiNameField.setDefaultEditMode(true).setRequired(true));
        form.add(peiAddressField.setDefaultEditMode(true).setRequired(true));
        form.add(peiEmailField.setDefaultEditMode(true).setRequired(true));
        form.add(peiPrincipalField.setDefaultEditMode(true).setRequired(true));
        peiTelephoneField.add(UIConstants.phoneNumberValidator);
        form.add(peiTelephoneField.setDefaultEditMode(true).setRequired(true));

        form.add(magazineCountField.setDefaultEditMode(true).setRequired(true));

        form.add(submitButton);

        formContainer.add(form);
        formContainer.add(requestSentLabel);
        formContainer.add(subscribeSpecialRequirements);
        add(formContainer.setOutputMarkupId(true));
    }

    private String getOrderNumber() {
        return new SimpleDateFormat("ddMM-HHmmss").format(new Date()).toString();
    }
}
