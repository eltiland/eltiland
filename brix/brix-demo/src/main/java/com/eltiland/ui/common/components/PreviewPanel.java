package com.eltiland.ui.common.components;

import com.eltiland.model.NewsItem;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for preview html data
 *
 * @author Pavel Androschuk
 */

public class PreviewPanel extends ELTDialogPanel {
    private IModel<String> bodyModel = new Model<>();
    private Label body = new Label("body", bodyModel);

    public PreviewPanel(String id) {
        super(id);

        body.setEscapeModelStrings(false);
        form.add(body);
    }

    public void setData(IModel<String> data) {
        bodyModel.setObject(data.getObject());
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        //
    }
}