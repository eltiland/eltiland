package com.eltiland.ui.common.column;

import com.inmethod.grid.column.PropertyColumn;
import org.apache.wicket.model.IModel;

/**
 * Property column with text wrapping and ability to set initial width.
 *
 * @author Aleksey Plotnikov.
 */
public class PropertyWrapColumn extends PropertyColumn {
    private int initialWidth = 0;

    /**
     * Column constructor.
     *
     * @param propertyExpression property expression.
     * @param headerModel        column header model.
     * @param sortProperty       sorting property.
     */
    public PropertyWrapColumn(IModel<String> headerModel, String propertyExpression, String sortProperty) {
        super(headerModel, propertyExpression, sortProperty);
    }

    /**
     * Column constructor with width initialization.
     *
     * @param propertyExpression property expression.
     * @param headerModel        column header model.
     * @param sortProperty       sorting property.
     * @param initialWidth       initial width.
     */
    public PropertyWrapColumn(IModel<String> headerModel, String propertyExpression,
                              String sortProperty, int initialWidth) {
        super(headerModel, propertyExpression, sortProperty);
        this.initialWidth = initialWidth;
    }

    @Override
    protected Object getProperty(Object object, String propertyExpression) {
        initialize(object);
        return super.getProperty(object, propertyExpression);
    }

    @Override
    public boolean getWrapText() {
        return true;
    }

    @Override
    public int getInitialSize() {
        if (initialWidth != 0) {
            return initialWidth;
        } else {
            return super.getInitialSize();
        }
    }

    /**
     * Initialization method. Override it to make some external initialization.
     */
    protected void initialize(Object object) {

    }
}
