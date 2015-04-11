package com.eltiland.ui.course.control.listeners.panel;

import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Panel for output listener name and status.
 *
 * @author Aleksey Plotnikov.
 */
public class NamePanel extends BaseEltilandPanel<User> {

    public NamePanel(String id, IModel<User> userIModel) {
        super(id, userIModel);
        add(new Label("name", getModelObject().getName()));
        boolean activated = (getModelObject().getConfirmationDate() != null);

        Label status = new Label("status", getString(activated ? "activated" : "no_activated"));
        add(status);
        status.add(new AttributeAppender("style", new Model<>(activated ? "color: darkgreen;" : "color: red"), ""));
    }
}
