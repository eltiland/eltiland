package com.eltiland.ui.webinars.components;

import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for webinar action.
 *
 * @author Aleksey Plotnikov
 */
public abstract class WebinarActionPanel extends BaseEltilandPanel {

    public enum ACTION {REG, UNREG}

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public WebinarActionPanel(String id, final ACTION action) {
        super(id);

        final boolean isReg = action.equals(ACTION.REG);

        WebMarkupContainer container = new WebMarkupContainer("image");
        container.add(new AttributeAppender("class", new Model<>(isReg ? "actionSignUp" : "actionLeave"), " "));
        container.add(new AttributeModifier("title", new ResourceModel(isReg ? "signup" : "leave")));
        container.add(new TooltipBehavior());
        container.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onAction(target);
            }
        });

        if (action.equals(ACTION.UNREG)) {
            container.add(new ConfirmationDialogBehavior(new ResourceModel("confirmationLeave")));
        }

        WebMarkupContainer container2 = new WebMarkupContainer("image2");
        container2.add(new AttributeAppender("class", new Model<>("actionSignUpMany"), " "));
        container2.add(new AttributeModifier("title", new ResourceModel("signupMany")));
        container2.add(new TooltipBehavior());
        container2.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (currentUserModel.getObject() != null) {
                    onActionMany(target);
                } else {
                    ELTAlerts.renderErrorPopup(getString("errorNoAuthorization"), target);
                }
            }
        });

        add(container);
        add(container2);
    }

    public abstract void onAction(AjaxRequestTarget target);

    public abstract void onActionMany(AjaxRequestTarget target);
}
