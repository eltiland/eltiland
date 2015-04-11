package com.eltiland.ui.slider.plugin;

import com.eltiland.model.Slider;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

/**
 * Paid Groups management panel.
 */
abstract class LinkPanel extends BaseEltilandPanel<Slider> {

    public LinkPanel(String id, IModel<Slider> sliderIModel) {
        super(id, sliderIModel);

        final String link = sliderIModel.getObject().getLink();

        add(new ExternalLink("link", link, link) {
            @Override
            public boolean isVisible() {
                return link != null && !link.isEmpty();
            }

            @Override
            public boolean isContextRelative() {
                return false;
            }
        });

        add(new EltiAjaxLink("change") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                LinkPanel.this.onClick(ajaxRequestTarget);
            }
        });
    }

    protected abstract void onClick(AjaxRequestTarget target);
}
