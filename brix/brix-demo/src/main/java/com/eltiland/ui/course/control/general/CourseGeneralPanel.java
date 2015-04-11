package com.eltiland.ui.course.control.general;

import com.eltiland.model.course2.ELTCourse;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * General information management panel for courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseGeneralPanel extends BaseEltilandPanel<ELTCourse> {

    private WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public CourseGeneralPanel(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);
        ELTTabMenu tabBar = new ELTTabMenu("tabPanel") {
            @Override
            public List<TabMenuData> getMenuItems() {
                List<TabMenuData> menuItems = new ArrayList<>();

                menuItems.add(new TabMenuData((short) 4, getMenuCaption((short) 4)));
                menuItems.add(new TabMenuData((short) 3, getMenuCaption((short) 3)));
                menuItems.add(new TabMenuData((short) 2, getMenuCaption((short) 2)));
                menuItems.add(new TabMenuData((short) 1, getMenuCaption((short) 1)));
                menuItems.add(new TabMenuData((short) 0, getMenuCaption((short) 0)));
                return menuItems;
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                if (index == 0) {
                    dataContainer.replace(new DataTab("dataPanel", CourseGeneralPanel.this.getModel()));
                } else if (index == 1) {
                    dataContainer.replace(new StartTab("dataPanel", CourseGeneralPanel.this.getModel()));
                } else if (index == 2) {
                    dataContainer.replace(new VideoTab("dataPanel", CourseGeneralPanel.this.getModel()));
                } else if (index == 3) {
                    dataContainer.replace(new ImageTab("dataPanel", CourseGeneralPanel.this.getModel()));
                } else if (index == 4) {
                    dataContainer.replace(new RegTab("dataPanel", CourseGeneralPanel.this.getModel()));
                }
                target.add(dataContainer);
            }
        };
        add(dataContainer.setOutputMarkupId(true));
        add(tabBar);
        dataContainer.add(new DataTab("dataPanel", getModel()));
    }

    private String getMenuCaption(short index) {
        switch (index) {
            case 0:
                return getString("data.menu");
            case 1:
                return getString("start.menu");
            case 2:
                return getString("video.menu");
            case 3:
                return getString("image.menu");
            case 4:
                return getString("reg.menu");
            default:
                return "";
        }
    }
}
