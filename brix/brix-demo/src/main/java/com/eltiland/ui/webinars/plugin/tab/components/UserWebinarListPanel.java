package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog panel with a list of webinars and records on the given user.
 *
 * @author Aleksey Plotnikov.
 */
public class UserWebinarListPanel extends ELTDialogPanel {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;
    @SpringBean
    private WebinarManager webinarManager;

    private IModel<User> userIModel = new GenericDBModel<>(User.class);

    private IModel<List<Webinar>> listModel = new LoadableDetachableModel<List<Webinar>>() {
        @Override
        protected List<Webinar> load() {
            List<Webinar> list = new ArrayList<>();

            User user = userIModel.getObject();
            if (user != null) {
                list = webinarManager.getWebinars(user.getEmail());
            }

            return list;
        }
    };

    public UserWebinarListPanel(String id) {
        super(id);
        form.add(new ListView<Webinar>("webinarList", listModel) {
            @Override
            protected void populateItem(ListItem<Webinar> item) {
                item.add(new InfoPanel("webinarPanel", item.getModel()));
            }
        });
    }

    public void initUserData(User user) {
        userIModel.setObject(user);
        listModel.detach();
    }

    @Override
    protected String getHeader() {
        return getString("webinarsHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    private class InfoPanel extends BaseEltilandPanel<Webinar> {
        protected InfoPanel(String id, IModel<Webinar> webinarIModel) {
            super(id, webinarIModel);
            String value = getModelObject().getName();
            add(new Label("webinarName", value));
        }
    }
}
