package com.eltiland.ui.common.column;

import com.eltiland.model.payment.PaidEntity;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Column for admin interface, displays price of Paid entity.
 *
 * @author Aleksey Plotnikov.
 */
public class GeneralPriceColumn<T, I extends PaidEntity> extends AbstractColumn<T, I> {
    public GeneralPriceColumn(String columnId, IModel<String> headerModel, String sortProperty) {
        super(columnId, headerModel, sortProperty);
    }

    public GeneralPriceColumn(String columnId, IModel<String> headerModel) {
        super(columnId, headerModel);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<I> rowModel) {
        String priceValue = getZeroPrice();
        if (rowModel.getObject() != null) {
            String tValue = rowModel.getObject().getPrice().toString();
            if (!(tValue.equals("null")) && !(tValue.equals("0.00"))) {
                priceValue = tValue + getSeparator() + getMonetaryUnit();
            }
        }

        return new Label(componentId, priceValue);
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
