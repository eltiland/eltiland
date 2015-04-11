package com.eltiland.ui.common;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.model.IModel;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * @author knorr
 * @version 1.0
 * @since 8/21/12
 */
public class EltiLocalizer extends Localizer {

    @Override
    public String getString(String key, Component component, IModel<?> model, Locale locale,
                            String style, String defaultValue) throws MissingResourceException {
        return super.getString(key, component, model, locale, style, defaultValue).replaceAll("(\\r|\\n)", " ");
    }
}
