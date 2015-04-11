package com.eltiland.ui.worktop.simple.panel;

import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import com.eltiland.ui.worktop.simple.panel.files.ProfileAvailablePanel;
import com.eltiland.ui.worktop.simple.panel.files.ProfileUserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User files panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileFilesPanel extends BaseEltilandPanel<User> {

    private WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");

    public ProfileFilesPanel(String id, IModel<User> userIModel) {
        super(id, userIModel);

        add(new ELTTabMenu("tab_menu") {
            @Override
            public List<TabMenuData> getMenuItems() {
                return new ArrayList<>(Arrays.asList(
                        new TabMenuData((short) 1, getMenuCaption((short) 1)),
                        new TabMenuData((short) 0, getMenuCaption((short) 0))));
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                switch (index) {
                    case 0:
                        dataContainer.replace(
                                new ProfileUserPanel("dataPanel", ProfileFilesPanel.this.getModel()));
                        break;
                    case 1:
                        dataContainer.replace(
                                new ProfileAvailablePanel("dataPanel", ProfileFilesPanel.this.getModel()));
                        break;
                }
                target.add(dataContainer);
            }
        });
        add(dataContainer.setOutputMarkupId(true));
        dataContainer.add(new ProfileUserPanel("dataPanel", getModel()));
    }

    private String getMenuCaption(short index) {
        switch (index) {
            case 1:
                return getString("available.menu");
            case 0:
                return getString("files.menu");
            default:
                return "";
        }
    }
}
