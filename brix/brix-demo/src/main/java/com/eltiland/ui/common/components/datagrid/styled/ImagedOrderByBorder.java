package com.eltiland.ui.common.components.datagrid.styled;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;

/**
 * Wraps content with link and adds order direction image.
 *
 * @author Aleksey Plotnikov
 */
class ImagedOrderByBorder extends Border {

    private WebMarkupContainer orderImage;
    private ISortStateLocator locator;
    private String property;

    public ImagedOrderByBorder(final DataTablePanel panel, String id, final String property,
                               final ISortStateLocator stateLocator,
                               OrderByLink.ICssProvider cssProvider) {
        super(id);

        this.locator = stateLocator;
        this.property = property;

        orderImage = new WebMarkupContainer("orderImage") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                tag.append("class", "iconNoSort", " ");
                SortOrder sortOrder = ImagedOrderByBorder.this.locator.getSortState()
                        .getPropertySortOrder(ImagedOrderByBorder.this.property);

                if (sortOrder == SortOrder.ASCENDING) {
                    tag.append("class", "iconSortUp", " ");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    tag.append("class", "iconSortDown", " ");
                }
            }
        };

        OrderByLink link = new AjaxFallbackOrderByLink("orderByLink", property, stateLocator) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(panel.getTable());
            }
        };

        addToBorder(link);
        link.add(orderImage);
        add(new OrderByLink.CssModifier(link, cssProvider));
        link.add(getBodyContainer());
    }

    public ImagedOrderByBorder(DataTablePanel panel, String id,
                               String property, ISortStateLocator stateLocator) {
        this(panel, id, property, stateLocator, OrderByLink.DefaultCssProvider.getInstance());
    }
}
