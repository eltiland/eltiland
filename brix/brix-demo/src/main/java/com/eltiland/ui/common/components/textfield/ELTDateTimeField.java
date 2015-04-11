package com.eltiland.ui.common.components.textfield;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Text field with date/time behaviour and feedback label.
 *
 * @author Aleksey Plotnikov
 */
public class ELTDateTimeField extends AbstractTextField<Date> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ELTDateTimeField.class);

    /**
     * Field constructor.
     *
     * @param id          markup id.
     * @param headerModel header model.
     * @param model       value model.
     * @param type        value type.
     */
    public ELTDateTimeField(String id, IModel<String> headerModel, IModel<Date> model, Class<Date> type) {
        super(id, headerModel, model, type);
    }

    /**
     * Field constructor.
     *
     * @param id          markup id.
     * @param headerModel header model.
     * @param model       value model.
     * @param type        value type.
     * @param isRequired  required flag.
     */
    public ELTDateTimeField(String id, IModel<String> headerModel,
                            IModel<Date> model, Class<Date> type, boolean isRequired) {
        super(id, headerModel, model, type, isRequired);
    }

    @Override
    protected FormComponent<Date> createEditor(IModel<Date> parentModel, Class<Date> type) {
        editorField = new TextField<Date>("textField", parentModel) {
            @Override
            protected void onBeforeRender() {
                add(new Behavior() {
                    @Override
                    public void renderHead(Component component, IHeaderResponse response) {
                        String mId = getMarkupId();
                        response.renderOnLoadJavaScript(String.format("$('#%s').datetimepicker({" +
                                "timeFormat: 'HH:mm'," +
                                "dateFormat: 'dd.mm.yy'})", mId));
                    }
                });
                super.onBeforeRender();
            }

            @Override
            protected Date convertValue(String[] value) throws ConversionException {
                DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
                try {
                    return df.parse(value[0]);
                } catch (ParseException e) {
                    LOGGER.error("Cannot parse date.", e);
                    throw new WicketRuntimeException("Cannot parse date.", e);
                }
            }
        };
        editorField.setOutputMarkupId(true);

        return editorField;
    }
}
