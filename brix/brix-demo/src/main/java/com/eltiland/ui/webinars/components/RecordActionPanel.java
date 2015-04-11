package com.eltiland.ui.webinars.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for webinar action.
 *
 * @author Aleksey Plotnikov
 */
public abstract class RecordActionPanel extends BaseEltilandPanel {

    public RecordActionPanel(String id) {
        super(id);

        EltiAjaxLink link = new EltiAjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAction(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };


        link.add(new AttributeModifier("title", new ResourceModel("recordInvoice")));
        link.add(new TooltipBehavior());

        WebMarkupContainer container = new WebMarkupContainer("image");
        container.add(new AttributeAppender("class", new Model<>("actionSignUp"), " "));

        link.add(container);
        add(link);
    }

    public abstract void onAction(AjaxRequestTarget target);
}
