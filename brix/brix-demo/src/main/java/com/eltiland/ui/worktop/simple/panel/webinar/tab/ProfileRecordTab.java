package com.eltiland.ui.worktop.simple.panel.webinar.tab;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.item.RecordItemPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab with webinars of user.
 */
public class ProfileRecordTab extends BaseEltilandPanel<User> {
    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<List<WebinarRecordPayment>> records
            = new LoadableDetachableModel<List<WebinarRecordPayment>>() {
        @Override
        protected List<WebinarRecordPayment> load() {
            genericManager.initialize(getModelObject(), getModelObject().getWebinarRecordPayments());
            return new ArrayList<>(getModelObject().getWebinarRecordPayments());
        }
    };

    public ProfileRecordTab(String id, IModel<User> userIModel) {
        super(id, userIModel);

        add(new ListView<WebinarRecordPayment>("recordList", records) {
            @Override
            protected void populateItem(ListItem<WebinarRecordPayment> item) {
                genericManager.initialize(item.getModelObject(), item.getModelObject().getRecord());
                item.add(new RecordItemPanel("recordInfoPanel",
                        new GenericDBModel<>(WebinarRecord.class, item.getModelObject().getRecord())));
            }
        });
    }
}
