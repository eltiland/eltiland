package com.eltiland.ui.common.components.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;

/**
 * A JavaScript confirmation dialog behavior. It installs itself prior to all  the onclick events and asks with a
 * jQuery dialog whether user is sure to proceed.
 * <p/>
 * The underlying function bindConfirmationDialog uses the jQuery undocumented features (storage of the events)
 * so may have problems with future migration. Migration from 1.4 to 1.7 was ok, so hoping for the best.
 * <p/>
 * Message may either be passed in construction time vai model or default will be used (resourceKey=
 * deleteConfirmationMessageDefault)
 *
 * @author Alex Cherednichenko
 */
public abstract class DialogBehavior extends SingleComponentBehavior {
    private IModel<String> messageModel;

    /**
     * Constructor with a message model. Will show that message instead of default.
     *
     * @param messageModel please use ResourceModel where possible.
     */
    public DialogBehavior(IModel<String> messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void bind(Component component) {
        super.bind(component);

        if (messageModel instanceof IComponentAssignedModel<?>) {
            this.messageModel = ((IComponentAssignedModel<String>) messageModel).wrapOnAssignment(component);
        }
    }

    @Override
    public void detach(Component component) {
        super.detach(component);

        messageModel.detach();
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        String messageToShow = messageModel.getObject();

        // as our buttons are really panels, we want to bind to an actual link, and not the panel itself.
        // otherwise, our click handler will fire _after_ the actual click action happens.
        String markupId = getComponent().getMarkupId();

        renderJavascriptDialog(response, messageToShow, markupId);
    }

    protected abstract void renderJavascriptDialog(IHeaderResponse response, String messageToShow, String markupId);

}
