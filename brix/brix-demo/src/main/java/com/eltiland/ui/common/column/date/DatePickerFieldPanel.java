package com.eltiland.ui.common.column.date;

import com.eltiland.ui.common.components.datepicker.DatePickerField;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * Panel with a {@link DatePickerField} that updates the property of the row immediately after user leaves the
 * field.
 *
 * @param <M> grid model object type
 * @param <I> row/item model object type
 */
public class DatePickerFieldPanel<M, I> extends EditableCellPanel<M, I, Date> {

    private final TextField<Date> field;

    /**
     * Constructor
     *
     * @param id     component id
     * @param model  model for the field
     * @param column column to which this panel belongs
     */
    public DatePickerFieldPanel(String id, final IModel<Date> model, IModel<I> rowModel,
                                AbstractColumn<M, I> column) {
        super(id, column, rowModel);

        field = new DatePickerField("textfield", model) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                if (!isValid()) {
                    tag.put("class", "imxt-invalid");
                    FeedbackMessage message = getFeedbackMessage();
                    if (message != null) {
                        tag.put("title", message.getMessage().toString());
                    }
                }
            }
        };
        field.setOutputMarkupId(true);
        field.setLabel(column.getHeaderModel());
        add(field);
    }

    @Override
    protected FormComponent<Date> getEditComponent() {
        return field;
    }

    private static final long serialVersionUID = 1L;

}
