package com.eltiland.ui.login.panels;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

/**
 * Panel for header of the portal with Login, Administration, Profile, Logout links.
 *
 * @author Aleksey Plotnikov.
 */
public class HeadSocialPanel extends BaseEltilandPanel {

    private final String CSS = "static/css/panels/head_login.css";

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    /*
   * *
    * Panel constructor.
    *
    * @param id markup id.
    */
    public HeadSocialPanel(String id) {
        super(id);

        WebMarkupContainer facebook = new WebMarkupContainer("facebook");
        WebMarkupContainer instagram = new WebMarkupContainer("instagram");

        facebook.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                throw new RedirectToUrlException("https://www.facebook.com/vdm.ru");
            }
        });

        instagram.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                throw new RedirectToUrlException("https://www.instagram.com/eltikudits");
            }
        });

        add(facebook);
        add(instagram);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
    }
}
