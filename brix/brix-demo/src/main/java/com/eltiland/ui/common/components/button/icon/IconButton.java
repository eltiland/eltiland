package com.eltiland.ui.common.components.button.icon;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * General button with icon.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class IconButton extends BaseEltilandPanel {

    /**
     * Panel ctor.
     *
     * @param id         markup id.
     * @param labelModel model of the label for button.
     * @param action     kind of the action for icon.
     */
    public IconButton(String id, IModel<String> labelModel, ButtonAction action) {
        super(id);

        WebMarkupContainer buttonPanel = new WebMarkupContainer("buttonPanel");
        buttonPanel.add(getBehavior());
        if (getAdditionalBehavior() != null) {
            buttonPanel.add(getAdditionalBehavior());
        }

        if (hasConfirmation()) {
            if (getConfirmationText() == null) {
                buttonPanel.add(new ConfirmationDialogBehavior());
            } else {
                buttonPanel.add(new ConfirmationDialogBehavior(getConfirmationText()));
            }
        }

        buttonPanel.add(new Label("label", labelModel));
        add(buttonPanel);

        WebMarkupContainer iconContainer = new WebMarkupContainer("iconContainer");
        iconContainer.add(new AttributeAppender("class", new Model<>(action.toString()), " "));
        buttonPanel.add(iconContainer);
    }

    /**
     * Onclick handler.
     */
    protected abstract void onClick(AjaxRequestTarget target);

    /**
     * @return decorator for button.
     */
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return null;
    }

    /**
     * @return ajax behavior for button.
     */
    protected AbstractAjaxBehavior getBehavior() {
        return new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                IconButton.this.onClick(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return IconButton.this.getAjaxCallDecorator();
            }
        };
    }

    protected AbstractAjaxBehavior getAdditionalBehavior() {
        return null;
    }

    protected boolean hasConfirmation() {
        return false;
    }

    protected IModel<String> getConfirmationText() {
        return null;
    }
}
