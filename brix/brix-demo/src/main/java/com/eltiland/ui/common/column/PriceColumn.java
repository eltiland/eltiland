package com.eltiland.ui.common.column;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * General price column.
 *
 * @author Aleksey Plotnikov.
 */
public class PriceColumn extends PropertyColumn {
    /**
     * Column constructor.
     *
     * @param displayModel       display model
     * @param sortProperty       sorting property.
     * @param propertyExpression wicket property expression used by PropertyModel.
     */
    public PriceColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Column constructor.
     *
     * @param displayModel       display model
     * @param propertyExpression wicket property expression used by PropertyModel.
     */
    public PriceColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(Item item, String componentId, IModel rowModel) {
        String priceValue = getZeroPrice();
        IModel priceModel = createLabelModel(rowModel);
        if (priceModel.getObject() != null) {
            String tValue = priceModel.getObject().toString();
            if (!(tValue.equals("null")) && !(tValue.equals("0.00"))) {
                priceValue = priceModel.getObject() + getSeparator() + getMonetaryUnit();
            }
        }

        item.add(new Label(componentId, priceValue));
    }

    protected String getZeroPrice() {
        return "0.00" + getSeparator() + getMonetaryUnit();
    }

    protected String getMonetaryUnit() {
        return "руб.";
    }

    protected String getSeparator() {
        return " ";
    }

}
