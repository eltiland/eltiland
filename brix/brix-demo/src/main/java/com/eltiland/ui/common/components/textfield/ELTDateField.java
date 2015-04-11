package com.eltiland.ui.common.components.textfield;

import com.eltiland.ui.common.components.datepicker.DatePickerField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * Text field with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public class ELTDateField extends AbstractTextField<Date> {

    public ELTDateField(String id, IModel<String> headerModel, IModel<Date> model) {
        super(id, headerModel, model, Date.class);
    }

    public ELTDateField(String id, IModel<String> headerModel, IModel<Date> model, boolean isRequired) {
        super(id, headerModel, model, Date.class, isRequired);
    }

    @Override
    protected FormComponent<Date> createEditor(IModel<Date> parentModel, Class<Date> type) {
        editorField = new DatePickerField("textField", parentModel) {
            @Override
            protected String renderJavaScriptOnSelect() {
                return String.format("function (dateText, inst) {$('#%s').text(dateText);}", editorField.getMarkupId());
            }
        };
        editorField.setOutputMarkupId(true);
        return editorField;
    }
}
