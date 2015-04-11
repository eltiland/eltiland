package com.eltiland.ui.common.column.date;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * Property column that uses a {@link com.eltiland.ui.common.column.date.DatePickerFieldPanel} as cell component when the item is selected.
 *
 * @author Matej Knopp
 */
public class EditableDatePropertyColumn<M, I> extends EditablePropertyColumn<M, I, Date> {
    public EditableDatePropertyColumn(String columnId, IModel<String> headerModel, String propertyExpression, String sortProperty) {
        super(columnId, headerModel, propertyExpression, sortProperty);
    }

    public EditableDatePropertyColumn(String columnId, IModel<String> headerModel, String propertyExpression) {
        super(columnId, headerModel, propertyExpression);
    }

    public EditableDatePropertyColumn(IModel<String> headerModel, String propertyExpression, String sortProperty) {
        super(headerModel, propertyExpression, sortProperty);
    }

    public EditableDatePropertyColumn(IModel<String> headerModel, String propertyExpression) {
        super(headerModel, propertyExpression);
    }

    @Override
    protected EditableCellPanel<M, I, Date> newCellPanel(String componentId, IModel<I> rowModel, IModel<Date> cellModel) {
        return new DatePickerFieldPanel<M, I>(componentId, cellModel, rowModel, this);
    }
}
