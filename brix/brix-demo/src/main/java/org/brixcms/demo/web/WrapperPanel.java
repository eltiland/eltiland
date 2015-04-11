package org.brixcms.demo.web;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.Component;

/**
 * @author knorr
 * @version 1.0
 * @since 8/28/12
 */
public class WrapperPanel extends BaseEltilandPanel {

    public static final String INNER_PANEL_ID = "innerPanel";

    public WrapperPanel(String id, Component innerPanel) {
        super(id);
        add(innerPanel);
    }
}
