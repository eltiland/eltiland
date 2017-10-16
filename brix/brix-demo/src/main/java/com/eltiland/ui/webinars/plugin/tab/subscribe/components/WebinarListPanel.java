package com.eltiland.ui.webinars.plugin.tab.subscribe.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Панель для вывода спсика вебинаров для абонемента.
 */
public class WebinarListPanel extends BaseEltilandPanel<WebinarSubscription> {

    @SpringBean
    private GenericManager genericManager;

    public WebinarListPanel(String id, IModel<WebinarSubscription> webinarSubscriptionIModel) {
        super(id, webinarSubscriptionIModel);

        genericManager.initialize(getModelObject(), getModelObject().getWebinars());

        ListView<Webinar> webinarListView = new ListView<Webinar>("webinarList", getModelObject().getWebinars()) {
            @Override
            protected void populateItem(ListItem<Webinar> listItem) {
                listItem.add(new Label("webinarInnerPanel", listItem.getModel().getObject().getName()));
            }
        };

        add(webinarListView);
    }
}
