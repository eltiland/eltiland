package com.eltiland.ui.subscribe;

import com.eltiland.bl.SubscriberManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.textfield.styled.ELTEmailPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Subscribe panel.
 *
 * @author Aleksey Plotnikov.
 */
public class SubscribePanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SubscribePanel.class);

    @SpringBean
    private SubscriberManager subscriberManager;

    public SubscribePanel(String id) {
        super(id);

        add(new ELTEmailPanel("subscribePanel") {
            @Override
            public void onSubmit(AjaxRequestTarget target, String value) {
                if (subscriberManager.checkSubscriberByEmail(value)) {
                    ELTAlerts.renderErrorPopup(getString("errorAlreadyExists"), target);
                } else {
                    Subscriber subscriber = new Subscriber();
                    subscriber.setEmail(value);
                    try {
                        subscriberManager.createSubscriber(subscriber);
                    } catch (SubscriberException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        return;
                    }
                    ELTAlerts.renderOKPopup(getString("subscriberAdded"), target);
                }
            }
        });
    }
}
