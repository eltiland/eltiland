package com.eltiland.ui.course.plugin.components;

import com.eltiland.model.course.paidservice.CoursePaidInvoice;
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
public abstract class InvoiceActionPanel extends BaseEltilandPanel<CoursePaidInvoice> {

    protected InvoiceActionPanel(String id, IModel<CoursePaidInvoice> coursePaidInvoiceIModel) {
        super(id, coursePaidInvoiceIModel);

        EltiAjaxLink applyButton = new EltiAjaxLink("applyButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("applyConfirmation")));
            }

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
                add(new ConfirmationDialogBehavior());
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

        add(applyButton);
        add(editButton);
        add(cancelButton);

        applyButton.add(new AttributeModifier("title", new ResourceModel("applyAction")));
        editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
        cancelButton.add(new AttributeModifier("title", new ResourceModel("cancelAction")));

        applyButton.add(new TooltipBehavior());
        editButton.add(new TooltipBehavior());
        cancelButton.add(new TooltipBehavior());
    }

    public abstract void onApply(AjaxRequestTarget target);

    public abstract void onEdit(AjaxRequestTarget target);

    public abstract void onCancel(AjaxRequestTarget target);
}
