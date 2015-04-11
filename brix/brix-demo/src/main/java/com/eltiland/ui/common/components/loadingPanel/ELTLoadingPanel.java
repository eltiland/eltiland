package com.eltiland.ui.common.components.loadingPanel;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;

/**
 * Loading indicator panel.
 *
 * @author Aleksey Plotnikov.
 */
abstract public class ELTLoadingPanel extends AjaxLazyLoadPanel {
    public ELTLoadingPanel(String id) {
        super(id);
    }

    @Override
    public Component getLoadingComponent(String markupId) {
        return new IndicatorPanel(markupId);
    }
}
