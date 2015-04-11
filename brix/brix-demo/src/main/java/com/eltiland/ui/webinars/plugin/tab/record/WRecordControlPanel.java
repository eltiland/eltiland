package com.eltiland.ui.webinars.plugin.tab.record;

import com.eltiland.bl.GenericManager;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Webinar record management panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class WRecordControlPanel extends BaseEltilandPanel<Webinar> {

    @SpringBean
    private GenericManager genericManager;

    public WRecordControlPanel(String id, IModel<Webinar> webinarIModel) {
        super(id, webinarIModel);

        EltiAjaxLink addButton = new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                genericManager.initialize(WRecordControlPanel.this.getModelObject(),
                        WRecordControlPanel.this.getModelObject().getRecord());
                return WRecordControlPanel.this.getModelObject().getRecord() == null;
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

            @Override
            public boolean isVisible() {
                genericManager.initialize(WRecordControlPanel.this.getModelObject(),
                        WRecordControlPanel.this.getModelObject().getRecord());
                return WRecordControlPanel.this.getModelObject().getRecord() != null;
            }
        };

        EltiAjaxLink deleteButton = new EltiAjaxLink("cancelButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onDelete(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        add(addButton);
        add(editButton);
        add(deleteButton);

        addButton.add(new AttributeModifier("title", new ResourceModel("addAction")));
        editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
        deleteButton.add(new AttributeModifier("title", new ResourceModel("сancelAction")));

        addButton.add(new TooltipBehavior());
        editButton.add(new TooltipBehavior());
        deleteButton.add(new TooltipBehavior());
    }

    public abstract void onAdd(AjaxRequestTarget target);

    public abstract void onEdit(AjaxRequestTarget target);

    public abstract void onDelete(AjaxRequestTarget target);
}
