package com.eltiland.ui.common;

import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.menu.ELTLeftMenu;
import com.eltiland.ui.news.component.NewsPanel;
import com.eltiland.ui.subscribe.SubscribePanel;
import com.eltiland.ui.usercount.UserCountPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Base page for three-column layout. Child tag in middle column.
 *
 * @param <T> the type of the page's model object
 */
public abstract class TwoColumnPage<T> extends OneColumnPage<T> {

    protected TwoColumnPage() {
    }

    protected TwoColumnPage(IModel<T> model) {
        super(model);
    }

    protected TwoColumnPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ELTLeftMenu("sidePanel", this.getClass()));
        add(new UserCountPanel("userCountPanel"));
        add(new SubscribePanel("subscribePanel"));
        add(new NewsPanel("newsPanel").setShortNewsList(true));

        WebMarkupContainer logo = new WebMarkupContainer("assist-logo");
        logo.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RedirectToUrlException("http://www.assist.ru");
            }
        });

        logo.add(new AttributeModifier("title", new ResourceModel("assist.tooltip")));
        logo.add(new TooltipBehavior());

        add(logo);
    }
}
