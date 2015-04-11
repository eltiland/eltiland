package com.eltiland.ui.faq.plugin.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Panel for changing number of the QA.
 *
 * @author Aleksey PLotnikov.
 */
public abstract class ChangeQANumberPanel extends BaseEltilandPanel<String> {
    /**
     * Panel constructor.
     *
     * @param id          panel's ID.
     * @param numberModel number string model.
     */
    public ChangeQANumberPanel(String id, IModel<String> numberModel) {
        super(id, numberModel);

        add(new Label("numberLabel", numberModel.getObject()));

        EltiAjaxLink firstLink = new EltiAjaxLink("upLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onChangeOrder(true, target);
            }
        };

        EltiAjaxLink secondLind = new EltiAjaxLink("downLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onChangeOrder((isUpAvailable() && !isDownAvailable()), target);
            }
        };

        add(firstLink);
        add(secondLind);

        WebMarkupContainer firstImage = new WebMarkupContainer("firstImage") {
            @Override
            public boolean isVisible() {
                return isUpAvailable() && isDownAvailable();
            }
        };
        WebMarkupContainer secondImage = new WebMarkupContainer("secondImage") {
            @Override
            public boolean isVisible() {
                return isUpAvailable() || isDownAvailable();
            }
        };

        if (isUpAvailable() && !isDownAvailable()) {
            secondImage.add(new AttributeModifier("class", "upImage"));
        }

        firstLink.add(firstImage);
        secondLind.add(secondImage);
    }

    public abstract boolean isUpAvailable();

    public abstract boolean isDownAvailable();

    public abstract void onChangeOrder(boolean isUp, AjaxRequestTarget target);
}
