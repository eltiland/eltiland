package com.eltiland.ui.course.components.editPanels.elements.test.edit.result;

import com.eltiland.model.course.test.TestJump;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Test jump action panel.
 *
 * @author Aleksey Plotnikov.
 */
abstract class JumpActionPanel extends BaseEltilandPanel<TestJump> {
    /**
     * Panel constructor.
     *
     * @param id             markup id.
     * @param testJumpIModel jump model.
     */
    protected JumpActionPanel(String id, IModel<TestJump> testJumpIModel) {
        super(id, testJumpIModel);

        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit(target);
            }
        };

        EltiAjaxLink upButton = new EltiAjaxLink("upButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onMoveUp(target);
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
            public boolean isVisible() {
                return canBeMovedDown();
            }
        };

        EltiAjaxLink removeButton = new EltiAjaxLink("removeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onDelete(target);
            }
        };

        editButton.add(new AttributeModifier("title", new ResourceModel("editTooltip")));
        editButton.add(new TooltipBehavior());
        removeButton.add(new AttributeModifier("title", new ResourceModel("removeTooltip")));
        removeButton.add(new TooltipBehavior());
        upButton.add(new AttributeModifier("title", new ResourceModel("upTooltip")));
        upButton.add(new TooltipBehavior());
        downButton.add(new AttributeModifier("title", new ResourceModel("downTooltip")));
        downButton.add(new TooltipBehavior());

        add(removeButton);
        add(editButton);
        add(upButton);
        add(downButton);
    }

    protected abstract void onEdit(AjaxRequestTarget target);

    protected abstract void onDelete(AjaxRequestTarget target);

    protected abstract void onMoveUp(AjaxRequestTarget target);

    protected abstract void onMoveDown(AjaxRequestTarget target);

    protected abstract boolean canBeMovedUp();

    protected abstract boolean canBeMovedDown();
}
