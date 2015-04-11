package com.eltiland.ui.forum.panels;

import com.eltiland.model.forum.ForumGroup;
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
 * Forum group action panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ForumGroupActionPanel extends BaseEltilandPanel<ForumGroup> {
    protected ForumGroupActionPanel(String id, IModel<ForumGroup> forumGroupIModel) {
        super(id, forumGroupIModel);

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
                return canBeAdded();
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
                return canBeDeleted();
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

            @Override
            public boolean isVisible() {
                return canBeDeleted();
            }
        };

        add(cancelButton);
        add(editButton);
        add(addButton);

        cancelButton.add(new AttributeModifier("title", new ResourceModel("removeAction")));
        editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
        addButton.add(new AttributeModifier("title", new ResourceModel("addAction")));

        cancelButton.add(new TooltipBehavior());
        editButton.add(new TooltipBehavior());
        addButton.add(new TooltipBehavior());
    }

    protected abstract void onDelete(AjaxRequestTarget target);

    protected abstract void onEdit(AjaxRequestTarget target);

    protected abstract void onAdd(AjaxRequestTarget target);

    protected abstract boolean canBeDeleted();

    protected abstract boolean canBeAdded();
}
