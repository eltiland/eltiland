package com.eltiland.ui.common;

import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Alex on 14.12.2016.
 */
public class ModultekaPage extends GenericWebPage {
    @Override
    protected void onInitialize() {
        super.onInitialize();
        throw new RedirectToUrlException("http://modulteka.mmr.lclients.ru");
    }

    public ModultekaPage() {
    }

    public ModultekaPage(IModel model) {
        super(model);
    }

    public ModultekaPage(PageParameters parameters) {
        super(parameters);
    }
}
