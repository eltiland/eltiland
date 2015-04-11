package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Webinar action panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class WebinarInvoiceActionPanel extends BaseEltilandPanel<Webinar> {
    /**
     * Panel constructor.
     *
     * @param id            panel's ID.
     * @param webinarIModel webinar model.
     */
    public WebinarInvoiceActionPanel(String id, IModel<Webinar> webinarIModel) {
        super(id, webinarIModel);

        EltiAjaxLink applyButton = new EltiAjaxLink("applyButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onApply(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };


        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink cancelButton = new EltiAjaxLink("cancelButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("cancelApplyMessage")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        add(editButton);
        add(cancelButton);
        add(applyButton);

        editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
        cancelButton.add(new AttributeModifier("title", new ResourceModel("сancelAction")));
        applyButton.add(new AttributeModifier("title", new ResourceModel("applyAction")));

        editButton.add(new TooltipBehavior());
        cancelButton.add(new TooltipBehavior());
        applyButton.add(new TooltipBehavior());
    }

    public abstract void onEdit(AjaxRequestTarget target);

    public abstract void onCancel(AjaxRequestTarget target);

    public abstract void onApply(AjaxRequestTarget target);
}