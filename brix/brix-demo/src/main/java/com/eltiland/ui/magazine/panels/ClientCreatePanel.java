package com.eltiland.ui.magazine.panels;

import com.eltiland.bl.SubscriberManager;
import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for input client information
 *
 * @author Aleksey Plotnikov.
 */
public class ClientCreatePanel extends BaseEltilandPanel<Client> implements IDialogNewCallback<Client> {

    @SpringBean
    private ClientManager clientManager;
    @SpringBean
    private SubscriberManager subscriberManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCreatePanel.class);

    private IDialogActionProcessor<Client> newCallback;

    private ELTTextField nameField =
            new ELTTextField("nameField", new ResourceModel("nameLabel"), new Model<String>(), String.class, true) {
                @Override
                protected int getInitialWidth() {
                    return 320;
                }
            };
    private ELTTextEmailField emailField =
            new ELTTextEmailField("emailField", new ResourceModel("emailLabel"), new Model<String>(), true) {
                @Override
                protected int getInitialWidth() {
                    return 320;
                }
            };
    private ELTTextField phoneField =
            new ELTTextField("phoneField", new ResourceModel("phoneLabel"), new Model<String>(), String.class) {
                @Override
                protected int getInitialWidth() {
                    return 320;
                }
            };

    private CheckBox subscribeField = new CheckBox("subscribeCheckBox", new Model<Boolean>(false));

    public ClientCreatePanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);
        form.add(new FormRequired("required"));

        form.add(new EltiAjaxSubmitLink("okButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form components) {
                Client client = new Client();
                client.setEmail(emailField.getModelObject());
                client.setName((String) nameField.getModelObject());
                client.setActive(true);
                client.setPhone((String) phoneField.getModelObject());
                try {
                    clientManager.createClient(client);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create client entity", e);
                    throw new WicketRuntimeException("Cannot create client entity", e);
                }

                if( subscribeField.getModelObject() ) {
                    Subscriber subscriber = new Subscriber();
                    subscriber.setEmail(emailField.getModelObject());
                    try {
                        subscriberManager.createSubscriber(subscriber);
                    } catch (SubscriberException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), ajaxRequestTarget);
                        return;
                    }
                }

                newCallback.process(new GenericDBModel<>(Client.class, client), ajaxRequestTarget);
            }
        });

        form.add(nameField);
        nameField.addMaxLengthValidator(256);
        phoneField.addMaxLengthValidator(40);
        emailField.addMaxLengthValidator(40);
        form.add(emailField);
        form.add(phoneField);
        form.add(subscribeField);
        phoneField.add(UIConstants.phoneNumberValidator);
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<Client> callback) {
        this.newCallback = callback;
    }
}
