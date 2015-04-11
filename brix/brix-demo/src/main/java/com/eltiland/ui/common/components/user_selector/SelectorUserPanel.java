package com.eltiland.ui.common.components.user_selector;

import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for output user, registered on webinar.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SelectorUserPanel extends BaseEltilandPanel<User> {

    private final String CSS = "static/css/panels/user_selector.css";

    protected SelectorUserPanel(String id, IModel<User> userModel) {
        super(id, userModel);

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

        add(new Label("name", userModel.getObject().getName()));
        add(cancelButton);

        cancelButton.add(new AttributeModifier("title", new ResourceModel("cancel")));
        cancelButton.add(new TooltipBehavior());
    }

    protected abstract void onCancel(AjaxRequestTarget target);

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
