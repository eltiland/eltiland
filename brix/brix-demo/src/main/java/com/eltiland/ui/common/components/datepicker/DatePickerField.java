package com.eltiland.ui.common.components.datepicker;


import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Input component with attached jquery DatePicker widget and specified date format validation.
 *
 * @author Ihor Cherednichenko
 * @version 1.0
 */
public class DatePickerField extends DateTextField {
    /**
     * jQuery UI Datepicker date format.
     */
    public static final String DATE_FORMAT_JS = "dd.mm.yy";
    private final Behavior datePickerBehavior = new Behavior() {
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String mId = getMarkupId();
            response.renderOnLoadJavaScript(String.format("CalendarWidget('%s', '%s', %s, %s)",
                    mId,
                    DATE_FORMAT_JS,
                    setMaxDateToNow,
                    renderJavaScriptOnSelect()));
        }
    };

    protected String renderJavaScriptOnSelect() {
        return "function(dateText, inst){}";
    }

    private boolean readOnly = false;
    private final AttributeModifier readOnlyBehavior
            = new AttributeModifier("readonly", new Model<String>("readonly"));
    private final boolean setMaxDateToNow;

    /**
     * Create DatePicker input component.  Parameter setMaxDateToNow is false.
     *
     * @param id    Wicket component id
     * @param model Component date
     */
    public DatePickerField(String id, IModel<Date> model) {
        this(id, model, false);
    }

    /**
     * Create DatePicker input component.
     *
     * @param id              Wicket component id
     * @param model           Component date
     * @param setMaxDateToNow flag. If true then datepicker disables possibility to choose date that in future.
     */
    public DatePickerField(String id, IModel<Date> model, boolean setMaxDateToNow) {
        super(id, model);
        this.setMaxDateToNow = setMaxDateToNow;
        setOutputMarkupId(true);
        add(new AttributeAppender("class", new Model<String>("date"), " "));
    }

    @Override
    public <C> IConverter getConverter(Class<C> type) {
        return new DateConverter() {
            private static final long serialVersionUID = 1L;

            /* *
            * @see org.apache.wicket.util.convert.converter.DateConverter#getDateFormat(java.util.Locale)
            */
            @Override
            public DateFormat getDateFormat(Locale locale) {
                if (locale == null) {
                    locale = Locale.getDefault();
                }
                return new SimpleDateFormat(UIConstants.DATE_FORMAT, locale);
            }

            @Override
            public Date convertToObject(String value, Locale locale) {
                Date date = super.convertToObject(value, locale);
                if (date == null) {
                    return null;
                }
                //To be sure that converted value fully match with displayed
                if (!value.equals(convertToString(date, locale))) {
                    String errorMessage = "Converted value not equals with displayed";
                    throw newConversionException(errorMessage, value, locale);
                }

                return date;
            }

            @Override
            protected ConversionException newConversionException(String message, Object value, Locale locale) {
                return new ConversionException(message)
                        .setSourceValue(value)
                        .setTargetType(getTargetType())
                        .setConverter(this)
                        .setLocale(locale)
                        .setResourceKey("datePickerConversionError");
            }
        };

    }


    @Override
    protected void onBeforeRender() {
        final List<? extends Behavior> behaviors = getBehaviors();
        if (behaviors.contains(readOnlyBehavior) && !isReadOnly()) {
            remove(readOnlyBehavior);
        }
        if (behaviors.contains(datePickerBehavior)) {
            remove(datePickerBehavior);
        }
        boolean isVisible = true;

        setVisible(isVisible);

        if (isVisible && !isReadOnly()) {
            add(datePickerBehavior);
        }

        //strongly recommended call at the end override (see javadoc)
        super.onBeforeRender();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets readOnly property.
     *
     * @param readOnly flag if field is read only.
     * @return this
     */
    public DatePickerField setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        add(readOnlyBehavior);
        return this;
    }
}