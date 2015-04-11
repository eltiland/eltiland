package com.eltiland.ui.tags.plugin.panels;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.model.ResourceModel;

/**
 * Tag category action panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TagEntityActionPanel extends BaseEltilandPanel {
    protected TagEntityActionPanel(String id) {
        super(id);

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

        EltiAjaxLink closeButton = new EltiAjaxLink("closeButton") {
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

        add(editButton);
        add(closeButton);

        editButton.add(new AttributeModifier("title", new ResourceModel("change")));
        closeButton.add(new AttributeModifier("title", new ResourceModel("delete")));

        editButton.add(new TooltipBehavior());
        closeButton.add(new TooltipBehavior());
    }

    protected abstract void onEdit(AjaxRequestTarget target);

    protected abstract void onDelete(AjaxRequestTarget target);
}
