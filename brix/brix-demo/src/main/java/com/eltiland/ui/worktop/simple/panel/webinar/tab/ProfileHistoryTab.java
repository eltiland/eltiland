package com.eltiland.ui.worktop.simple.panel.webinar.tab;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.item.WebinarItemPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Tab with webinar history of given user.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileHistoryTab extends BaseEltilandPanel<User> {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<List<WebinarUserPayment>> paysWebinars = new LoadableDetachableModel<List<WebinarUserPayment>>() {
        @Override
        protected List<WebinarUserPayment> load() {
            return webinarUserPaymentManager.getWebinarPayments(getModelObject(), true, true);
        }
    };

    public ProfileHistoryTab(String id, IModel<User> userIModel) {
        super(id, userIModel);

        add(new ListView<WebinarUserPayment>("webinarHistoryList", paysWebinars) {
            @Override
            protected void populateItem(ListItem<WebinarUserPayment> item) {
                genericManager.initialize(item.getModelObject(), item.getModelObject().getWebinar());
                item.add(new WebinarItemPanel("webinarHistoryInfoPanel",
                        new GenericDBModel<>(Webinar.class, item.getModelObject().getWebinar())));
            }
        });
    }
}
