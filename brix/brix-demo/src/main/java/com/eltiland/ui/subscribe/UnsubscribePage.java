package com.eltiland.ui.subscribe;

import com.eltiland.bl.SubscriberManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.ui.common.HomePage;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Unsubscribe page.
 *
 * @author Aleksey PLotnikov.
 */
public class UnsubscribePage extends TwoColumnPage {

    protected static final Logger LOGGER = LoggerFactory.getLogger(UnsubscribePage.class);

    @SpringBean
    private SubscriberManager subscriberManager;

    public static final String MOUNT_PATH = "/unsubscribe";

    public UnsubscribePage(PageParameters parameters) {
        super(parameters);

        final String code = parameters.get(UrlUtils.UNSUBSCRIBE_CODE_PARAMETER_NAME).toString();

        if (code != null) {
            final Subscriber subscribe = subscriberManager.getSubscriberByUnCode(code);
            if (subscribe != null) {
                add(new EltiAjaxLink("yesButton") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            subscriberManager.deleteSubscriber(subscribe);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        EltiStaticAlerts.registerOKPopup(getString("unsubscribeMessage"));
                        throw new RestartResponseException(HomePage.class);
                    }
                });
                add(new EltiAjaxLink("noButton") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        throw new RestartResponseException(HomePage.class);
                    }
                });
            } else {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
