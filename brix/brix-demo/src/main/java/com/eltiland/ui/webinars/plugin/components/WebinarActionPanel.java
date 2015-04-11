package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.bl.WebinarUserPaymentManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Webinar action panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class WebinarActionPanel extends BaseEltilandPanel<Webinar> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebinarActionPanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;


    /**
     * Panel constructor.
     *
     * @param id            panel's ID.
     * @param webinarIModel webinar model.
     */
    public WebinarActionPanel(String id, IModel<Webinar> webinarIModel) {
        super(id, webinarIModel);

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

        EltiAjaxLink addButton = new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink closeButton = new EltiAjaxLink("closeButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("closeApplyMessage")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCloseRegistration(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeClosed();
            }
        };

        EltiAjaxLink openButton = new EltiAjaxLink("openButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("openApplyMessage")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onOpenRegistration(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeOpened();
            }
        };

        EltiAjaxLink settingsButton = new EltiAjaxLink("settingsButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onModeration(target);
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
        add(closeButton);
        add(openButton);
        add(settingsButton);
        add(cancelButton);
        add(addButton);

        editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
        closeButton.add(new AttributeModifier("title", new ResourceModel("closeRegAction")));
        openButton.add(new AttributeModifier("title", new ResourceModel("openRegAction")));
        settingsButton.add(new AttributeModifier("title", new ResourceModel("moderationAction")));
        cancelButton.add(new AttributeModifier("title", new ResourceModel("сancelAction")));

        editButton.add(new TooltipBehavior());
        closeButton.add(new TooltipBehavior());
        openButton.add(new TooltipBehavior());
        settingsButton.add(new TooltipBehavior());
        cancelButton.add(new TooltipBehavior());
    }

    public abstract void onEdit(AjaxRequestTarget target);

    public abstract void onAdd(AjaxRequestTarget target);

    public abstract void onCloseRegistration(AjaxRequestTarget target);

    public abstract void onOpenRegistration(AjaxRequestTarget target);

    public abstract void onModeration(AjaxRequestTarget target);

    public abstract void onCancel(AjaxRequestTarget target);

    public abstract boolean canBeClosed();

    public abstract boolean canBeOpened();
}