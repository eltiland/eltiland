package com.eltiland.ui.slider.plugin;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.model.ResourceModel;

/**
 * Slider action panel.
 *
 * @author Aleksey Plotnikov.
 */
abstract class ActionPanel extends BaseEltilandPanel {

    /**
     * Panel constructor.
     *
     * @param id            panel's ID.
     */
    public ActionPanel(String id) {
        super(id);

        EltiAjaxLink upButton = new EltiAjaxLink("upButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onMoveUp(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeMovedUp();
            }
        };

        EltiAjaxLink downButton = new EltiAjaxLink("downButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onMoveDown(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeMovedDown();
            }
        };

        EltiAjaxLink cancelButton = new EltiAjaxLink("removeButton") {
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

        add(upButton);
        add(downButton);
        add(cancelButton);

        upButton.add(new AttributeModifier("title", new ResourceModel("upAction")));
        downButton.add(new AttributeModifier("title", new ResourceModel("downAction")));
        cancelButton.add(new AttributeModifier("title", new ResourceModel("removeAction")));

        upButton.add(new TooltipBehavior());
        downButton.add(new TooltipBehavior());
        cancelButton.add(new TooltipBehavior());
    }

    public abstract void onMoveUp(AjaxRequestTarget target);

    public abstract void onMoveDown(AjaxRequestTarget target);

    public abstract void onDelete(AjaxRequestTarget target);

    public abstract boolean canBeMovedUp();

    public abstract boolean canBeMovedDown();
}