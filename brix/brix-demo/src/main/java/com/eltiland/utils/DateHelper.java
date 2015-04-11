package com.eltiland.utils;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

/**
 * Date convert helper.
 *
 * @author Aleksey Plotnikov.
 */
class DateHelper {
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    DateHelper() {
        Injector.get().inject(this);
    }

    int getShift() {
        return Integer.parseInt(eltilandProps.getProperty("time.shift"));
    }
}
