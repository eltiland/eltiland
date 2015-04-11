package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Eltiland main menu.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTMenu extends BaseEltilandPanel {
    protected ELTMenu(String id, final Class<? extends BaseEltilandPage> clazz) {
        super(id);

        WebMarkupContainer menuContainer = new WebMarkupContainer("menuContainer");

        ListView<MenuData> menuList = new ListView<MenuData>("itemList", getMenuItems()) {
            @Override
            protected void populateItem(ListItem<MenuData> item) {
                BaseEltilandPanel panel;
                panel = new ELTMenuItem("item",
                        item.getModelObject().getClazz(),
                        StringUtils.replace(item.getModelObject().getCaption(), "\\n", "\n"));
                if (clazz.equals(item.getModelObject().getClazz())) {
                    panel.add(new AttributeAppender("class", new Model<>("menu-item-active"), " "));
                }
                item.add(panel);
            }
        };

        menuContainer.add(menuList);
        if (getListStyle() != null) {
            menuList.add(new AttributeAppender("class", new Model<>(getListStyle()), " "));
        }

        add(menuContainer);
        if (getMenuStyle() != null) {
            menuContainer.add(new AttributeAppender("class", new Model<>(getMenuStyle()), " "));
        }
    }

    protected abstract IModel<List<MenuData>> getMenuItems();

    protected String getListStyle() {
        return null;
    }

    protected String getMenuStyle() {
        return null;
    }

    /**
     * Eltiland simple menu item class.
     */
    private class ELTMenuItem extends BaseEltilandPanel {
        /**
         * Panel constructor.
         *
         * @param id markup id.
         */
        public ELTMenuItem(String id, final Class<? extends BaseEltilandPage> clazz, String caption) {
            super(id);

            AjaxLink link = new AjaxLink("link") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    setResponsePage(clazz);
                }
            };
            link.add(new MultiLineLabel("linkCaption", caption).setEscapeModelStrings(false));
            add(link);
        }
    }
}
