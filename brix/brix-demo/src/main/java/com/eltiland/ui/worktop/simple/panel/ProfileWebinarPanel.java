package com.eltiland.ui.worktop.simple.panel;

import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import com.eltiland.ui.worktop.simple.panel.webinar.tab.ProfileHistoryTab;
import com.eltiland.ui.worktop.simple.panel.webinar.tab.ProfileRecordTab;
import com.eltiland.ui.worktop.simple.panel.webinar.tab.ProfileWebinarTab;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General courses panel for profile.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileWebinarPanel extends BaseEltilandPanel<User> {

    private WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");

    public ProfileWebinarPanel(String id, IModel<User> userModel) {
        super(id, userModel);

        add(new ELTTabMenu("tab_menu") {
            @Override
            public List<TabMenuData> getMenuItems() {
                return new ArrayList<>(Arrays.asList(
                        new TabMenuData((short) 2, getMenuCaption((short) 2)),
                        new TabMenuData((short) 1, getMenuCaption((short) 1)),
                        new TabMenuData((short) 0, getMenuCaption((short) 0))));
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                switch (index) {
                    case 0:
                        dataContainer.replace(new ProfileWebinarTab("dataPanel", ProfileWebinarPanel.this.getModel()));
                        break;
                    case 1:
                        dataContainer.replace(new ProfileHistoryTab("dataPanel", ProfileWebinarPanel.this.getModel()));
                        break;
                    case 2:
                        dataContainer.replace(new ProfileRecordTab("dataPanel", ProfileWebinarPanel.this.getModel()));
                        break;
                }
                target.add(dataContainer);
            }
        });

        add(dataContainer.setOutputMarkupId(true));
        dataContainer.add(new ProfileWebinarTab("dataPanel", getModel()));
    }

    private String getMenuCaption(short index) {
        switch (index) {
            case 2:
                return getString("records.menu");
            case 1:
                return getString("history.menu");
            case 0:
                return getString("webinars.menu");
            default:
                return "";
        }
    }
}
