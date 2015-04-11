package com.eltiland.ui.common.components.loadingPanel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Loading Indicator Panel.
 *
 * @author Aleksey Plotnikov.
 */
class IndicatorPanel extends Panel {
    public IndicatorPanel(String id) {
        super(id);
        add(new WebMarkupContainer("image"));
    }
}