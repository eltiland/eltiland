package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for output users, which are not present in webinar.
 *
 * @author Aleksey Plotnikov.
 */
abstract class WebinarCheckErrorPanel extends BaseEltilandPanel {

    private IModel<List<WebinarUserPayment>> paymentsModel = new GenericDBListModel<>(WebinarUserPayment.class);

    public WebinarCheckErrorPanel(String id) {
        super(id);

        add(new ListView<WebinarUserPayment>("userList", paymentsModel) {
            @Override
            protected void populateItem(ListItem<WebinarUserPayment> item) {
                WebinarUserPayment payment = item.getModelObject();
                item.add(new Label("userData",
                        payment.getUserSurname() + " " + payment.getUserName() + " (" + payment.getUserEmail() + ")"));
            }
        });

        add(new EltiAjaxLink("sendButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAddUsers(target, paymentsModel.getObject());
            }
        });
    }

    public void initUserList(List<WebinarUserPayment> payments) {
        paymentsModel.setObject(payments);
    }

    public abstract void onAddUsers(AjaxRequestTarget target, List<WebinarUserPayment> payments);
}
