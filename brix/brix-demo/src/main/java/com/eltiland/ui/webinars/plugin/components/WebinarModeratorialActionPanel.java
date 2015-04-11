package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Webinar action panel.
 *
 * @author Aleksey Plotnikov.
 */
abstract class WebinarModeratorialActionPanel extends BaseEltilandPanel<WebinarUserPayment> {
    /**
     * Panel constructor.
     *
     * @param id                       panel's ID.
     * @param webinarUserPaymentIModel webinar user payment model.
     */
    public WebinarModeratorialActionPanel(String id, IModel<WebinarUserPayment> webinarUserPaymentIModel) {
        super(id, webinarUserPaymentIModel);

        add(new EltiAjaxLink("denyAction") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("denyApplyMessage")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onDeny(target);
            }
        });
    }
    public abstract void onDeny(AjaxRequestTarget target);
}
