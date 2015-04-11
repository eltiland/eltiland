package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for changing role of user in webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarChangeRolePanel extends BaseEltilandPanel<WebinarUserPayment>
        implements IDialogUpdateCallback<WebinarUserPayment> {

    private IDialogActionProcessor<WebinarUserPayment> updateCallback;

    ELTAjaxCheckBox moderatorCheckBox = new ELTAjaxCheckBox(
            "moderatorCheckBox", new ResourceModel("moderator"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            if (getConvertedInput()) {
                memberCheckBox.setModelObject(false);
                observerCheckBox.setModelObject(false);
                target.add(memberCheckBox);
                target.add(observerCheckBox);
            }
        }
    };

    ELTAjaxCheckBox memberCheckBox = new ELTAjaxCheckBox(
            "memberCheckBox", new ResourceModel("member"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            if (getConvertedInput()) {
                moderatorCheckBox.setModelObject(false);
                observerCheckBox.setModelObject(false);
                target.add(moderatorCheckBox);
                target.add(observerCheckBox);
            }
        }
    };

    ELTAjaxCheckBox observerCheckBox = new ELTAjaxCheckBox(
            "observerCheckBox", new ResourceModel("observer"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            if (getConvertedInput()) {
                memberCheckBox.setModelObject(false);
                moderatorCheckBox.setModelObject(false);
                target.add(memberCheckBox);
                target.add(moderatorCheckBox);
            }
        }
    };

    /**
     * Panel constructor.
     *
     * @param id                       panel's ID.
     * @param webinarUserPaymentIModel user payment model.
     */
    public WebinarChangeRolePanel(String id, IModel<WebinarUserPayment> webinarUserPaymentIModel) {
        super(id, webinarUserPaymentIModel);

        Form form = new Form("form");
        add(form);

        WebinarUserPayment.Role role = getModelObject().getRole();
        moderatorCheckBox.setModelObject(role.equals(WebinarUserPayment.Role.MODERATOR));
        memberCheckBox.setModelObject(role.equals(WebinarUserPayment.Role.MEMBER));
        observerCheckBox.setModelObject(role.equals(WebinarUserPayment.Role.OBSERVER));

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                IModel<WebinarUserPayment> paymentModel = WebinarChangeRolePanel.this.getModel();
                if (moderatorCheckBox.getModelObject()) {
                    paymentModel.getObject().setRole(WebinarUserPayment.Role.MODERATOR);
                } else if (memberCheckBox.getModelObject()) {
                    paymentModel.getObject().setRole(WebinarUserPayment.Role.MEMBER);
                } else if (observerCheckBox.getModelObject()) {
                    paymentModel.getObject().setRole(WebinarUserPayment.Role.OBSERVER);
                }

                if (updateCallback != null) {
                    updateCallback.process(paymentModel, target);
                }
            }
        });

        form.add(moderatorCheckBox);
        form.add(memberCheckBox);
        form.add(observerCheckBox);
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<WebinarUserPayment> callback) {
        this.updateCallback = callback;
    }
}
