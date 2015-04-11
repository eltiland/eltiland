package com.eltiland.ui.common;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Base page for two-column layout. Child tag in left column.
 *
 * @param <T> the type of the page's model object
 */
public class OneColumnPage<T> extends BaseEltilandPage<T> {

    protected OneColumnPage() {
        super();
    }

    protected OneColumnPage(IModel<T> model) {
        super(model);
    }

    protected OneColumnPage(PageParameters parameters) {
        super(parameters);
    }
}
