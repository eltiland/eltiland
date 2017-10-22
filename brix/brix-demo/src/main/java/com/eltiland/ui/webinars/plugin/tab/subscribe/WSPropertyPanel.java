package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionManager;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.tab.subscribe.components.SubWebinarSelector;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Webinar subscripion property panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WSPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<WebinarSubscription>, IDialogUpdateCallback<WebinarSubscription> {

    @SpringBean
    private WebinarSubscriptionManager webinarSubscriptionManager;
    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(WSPropertyPanel.class);

    private boolean mode;

    private IModel<WebinarSubscription> webinarSubscriptionIModel = new GenericDBModel<>(WebinarSubscription.class);

    private IDialogUpdateCallback.IDialogActionProcessor<WebinarSubscription> updateCallback;
    private IDialogNewCallback.IDialogActionProcessor<WebinarSubscription> newCallback;

    private ELTTextField<String> nameField = new ELTTextField<>(
            "name", new ResourceModel("nameLabel"), new Model<String>(), String.class, true);

    private ELTTextArea descField = new ELTTextArea("description", new ResourceModel("descLabel"), new Model<String>()) {
        @Override
        protected int getMaxLength() {
            return 370;
        }

        @Override
        protected int getInitialHeight() {
            return 60;
        }
    };

    private PriceField priceField = new PriceField("price", new ResourceModel("priceLabel"), new Model<BigDecimal>());

    private SubWebinarSelector selector = new SubWebinarSelector("selector",
            new GenericDBListModel<>(Webinar.class));

    public WSPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(descField);
        form.add(priceField);
        form.add(selector);
        selector.setModelObject(new ArrayList<Webinar>());
    }

    public void initCreateMode() {
        mode = false;
        nameField.setModelObject(StringUtils.EMPTY_STRING);
        descField.setModelObject(StringUtils.EMPTY_STRING);
        priceField.setModelObject(BigDecimal.ZERO);
        selector.setModelObject(null);
        selector.setWebinars(new ArrayList<Webinar>());
    }

    public void initEditMode(WebinarSubscription webinarSubscription) {
        mode = true;

        genericManager.initialize(webinarSubscription, webinarSubscription.getWebinars());

        nameField.setModelObject(webinarSubscription.getName());
        descField.setModelObject(webinarSubscription.getInfo());
        priceField.setModelObject(webinarSubscription.getPrice());
        selector.setModelObject(webinarSubscription.getWebinars());
        selector.setWebinars(webinarSubscription.getWebinars());
        webinarSubscriptionIModel.setObject(webinarSubscription);
    }

    @Override
    protected String getHeader() {
        return getString(mode ? "editHeader" : "createHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        switch(event) {
            case Create:
            {
                WebinarSubscription subscription = new WebinarSubscription();
                subscription.setName(nameField.getModelObject());
                subscription.setInfo(descField.getModelObject());
                subscription.setPrice(priceField.getModelObject());
                subscription.setWebinars(selector.getModelObject());

                try {
                    webinarSubscriptionManager.create(subscription);
                } catch (WebinarException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    LOGGER.error(e.getMessage(), e);
                }

                newCallback.process(new GenericDBModel<>(WebinarSubscription.class, subscription), target);
                break;
            }
            case Save:
            {
                WebinarSubscription subscription = webinarSubscriptionIModel.getObject();
                subscription.setName(nameField.getModelObject());
                subscription.setInfo(descField.getModelObject());
                subscription.setPrice(priceField.getModelObject());
                subscription.setWebinars(selector.getModelObject());

                try {
                    webinarSubscriptionManager.update(subscription);
                } catch (WebinarException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    LOGGER.error(e.getMessage(), e);
                }

                updateCallback.process(new GenericDBModel<>(WebinarSubscription.class, subscription), target);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        if (event.equals(EVENT.Create)) {
            return !mode;
        } else {
            return mode;
        }
    }


    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<WebinarSubscription> callback) {
        this.newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<WebinarSubscription> callback) {
        this.updateCallback = callback;
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
