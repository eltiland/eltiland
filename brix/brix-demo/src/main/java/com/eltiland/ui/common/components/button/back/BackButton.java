package com.eltiland.ui.common.components.button.back;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Back button.
 *
 * @author Aleksey Plotnikov.
 */
public class BackButton extends BaseEltilandPanel {
    public BackButton(String id) {
        super(id);

        add(new EltiAjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAction(target);
            }
        });
    }

    protected void onAction(AjaxRequestTarget target) {
        target.prependJavaScript("history.go(-1)");
    }
}
