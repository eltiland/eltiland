package com.eltiland.ui.course.control.data.components;

import com.eltiland.model.course2.ContentStatus;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

/**
 * Version switch control.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VersionSwitch extends BaseEltilandPanel {

    private WebMarkupContainer demoButton = new WebMarkupContainer("demoButton");
    private WebMarkupContainer fullButton = new WebMarkupContainer("fullButton");

    private ContentStatus status;

    private final static String PUSH = "push";
    private final static String FREE = "free";
    private final static String GENERAL_CLASS_LEFT = "left-button";
    private final static String GENERAL_CLASS_RIGHT = "right-button";

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public VersionSwitch(String id, ContentStatus initialStatus) {
        super(id);

        status = initialStatus;
        add(demoButton.setOutputMarkupId(true));
        add(fullButton.setOutputMarkupId(true));

        demoButton.add(new AttributeAppender("class",
                new Model<>(status.equals(ContentStatus.DEMO) ? PUSH : FREE), " "));
        fullButton.add(new AttributeAppender("class",
                new Model<>(status.equals(ContentStatus.FULL) ? PUSH : FREE), " "));

        demoButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                status = ContentStatus.DEMO;
                buttonUpdate();
                target.add(demoButton);
                target.add(fullButton);
                onClick(target, ContentStatus.DEMO);
            }
        });

        fullButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                status = ContentStatus.FULL;
                buttonUpdate();
                target.add(demoButton);
                target.add(fullButton);
                onClick(target, ContentStatus.FULL);
            }
        });
    }

    private void buttonUpdate() {
        demoButton.add(new AttributeModifier("class",
                new Model<>(GENERAL_CLASS_LEFT + " " + (status.equals(ContentStatus.DEMO) ? PUSH : FREE))));
        fullButton.add(new AttributeModifier("class",
                new Model<>(GENERAL_CLASS_RIGHT + " " + (status.equals(ContentStatus.FULL) ? PUSH : FREE))));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_VERSION_SWITCH);
    }

    protected abstract void onClick(AjaxRequestTarget target, ContentStatus newStatus);
}
