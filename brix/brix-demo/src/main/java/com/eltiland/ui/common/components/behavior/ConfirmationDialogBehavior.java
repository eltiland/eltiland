package com.eltiland.ui.common.components.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

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
public class ConfirmationDialogBehavior extends DialogBehavior {

    private IModel<String> headerModel;

    @Override
    public void detach(Component component) {
        headerModel.detach();
        super.detach(component);
    }

    /**
     * Default constructor. Default message about 'deletion can not be undone!' is issued.
     */
    public ConfirmationDialogBehavior() {
        super(new ResourceModel("confirmationMessageDefault"));
        headerModel = new StringResourceModel("confirmationDialogHeaderLabel", getComponent(), null);
    }

    /**
     * Constructor with a message model. Will show that message instead of default.
     *
     * @param messageModel please use ResourceModel where possible.
     */
    public ConfirmationDialogBehavior(IModel<String> messageModel) {
        super(messageModel);
        headerModel = new StringResourceModel("confirmationDialogHeaderLabel", getComponent(), null);
    }

    /**
     * Constructor with a message model and header model. Will show that message instead of default.
     *
     * @param messageModel please use ResourceModel where possible.
     */
    public ConfirmationDialogBehavior(IModel<String> messageModel, IModel<String> headerModel) {
        super(messageModel);
        this.headerModel = headerModel;
    }

    protected void renderJavascriptDialog(IHeaderResponse response, String messageToShow, String markupId) {
        response.renderOnDomReadyJavaScript(
                String.format("bindConfirmationDialog('#%s', '%s', '%s')",
                        markupId,
                        messageToShow,
                        headerModel.getObject()));
    }
}
