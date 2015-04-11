package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Menu for dividing page on several sub pages.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTTabMenu extends BaseEltilandPanel {

    private MenuItem previousItem;

    private WebMarkupContainer menuContainer = new WebMarkupContainer("menuContainer");
    private short currentIndex;

    private final String CSS = "static/css/panels/tab_menu.css";

    public ELTTabMenu(String id) {
        super(id);

        add(menuContainer.setOutputMarkupPlaceholderTag(true));

        currentIndex = 0;
        menuContainer.add(new ListView<TabMenuData>("menu", getMenuItems()) {
            @Override
            protected void populateItem(final ListItem<TabMenuData> item) {
                boolean isActive = item.getModelObject().getIndex() == currentIndex;

                MenuItem menuItem = new MenuItem(
                        "menuItem", new Model<>(item.getModelObject().getCaption()), isActive) {
                    @Override
                    protected void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        ELTTabMenu.this.onClick(item.getModelObject().getIndex(), ajaxRequestTarget);
                    }

                    @Override
                    public boolean isVisible() {
                        return ELTTabMenu.this.isVisible(item.getModelObject().getIndex());
                    }
                };

                if (isActive) {
                    previousItem = menuItem;
                }
                item.add(menuItem);
            }
        });
    }

    /**
     * @return menu items.
     */
    public abstract List<TabMenuData> getMenuItems();

    /**
     * On click handler.
     *
     * @param index  (index of the menu item).
     * @param target ajax request target.
     */
    public abstract void onClick(short index, AjaxRequestTarget target);

    /**
     * @return TRUE if menu item with given index should be shown.
     */
    public boolean isVisible(short index) {
        return true;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    /**
     * Internal class for menu item.
     */
    private abstract class MenuItem extends BaseEltilandPanel {

        private WebMarkupContainer itemContainer = new WebMarkupContainer("item");

        public MenuItem(String id, IModel<String> labelModel, boolean active) {
            super(id);
            setOutputMarkupId(true);

            add(itemContainer);
            if (active) {
                itemContainer.add(new AttributeAppender("class", new Model<>("active_item"), " "));
            }

            itemContainer.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                    previousItem.setActive(false);
                    setActive(true);
                    ajaxRequestTarget.add(previousItem);
                    ajaxRequestTarget.add(MenuItem.this);
                    previousItem = MenuItem.this;

                    onClick(ajaxRequestTarget);
                }
            });

            itemContainer.add(new Label("label", labelModel));
        }

        public void setActive(boolean active) {
            itemContainer.add(new AttributeModifier("class", active ? "menu_item active_item" : "menu_item"));
        }

        protected abstract void onClick(AjaxRequestTarget ajaxRequestTarget);
    }
}
