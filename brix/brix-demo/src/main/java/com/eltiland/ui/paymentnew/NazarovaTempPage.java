package com.eltiland.ui.paymentnew;

import com.eltiland.ui.common.OneColumnPage;
import com.eltiland.ui.common.components.button.paybutton.PayNButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by Aleks on 05.06.2015.
 */
public class NazarovaTempPage extends OneColumnPage {

    public static final String MOUNT_PATH = "/nazarova_bill";

    public static final String CSS_PAYMENT_PAGE = "static/css/panels/payment_page.css";

    public NazarovaTempPage(PageParameters parameters) {
        super(parameters);
        String className = "ELTCourseListener";

        add(new Label("header", getString("pay.for") + getString(className + ".type")));

        WebMarkupContainer image = new WebMarkupContainer("image");
        image.add(new AttributeAppender("class", new ResourceModel(className + ".class"), " "));
        add(image);

        add(new Label("name", new ResourceModel("name")));
        Label desc = new Label("description", new ResourceModel("desc"));
        add(desc);

        add(new Label("value", "2332"));

        add(new PayNButton("pay_button"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS_PAYMENT_PAGE);
    }
}
