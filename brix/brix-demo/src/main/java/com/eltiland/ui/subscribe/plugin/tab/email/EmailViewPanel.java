package com.eltiland.ui.subscribe.plugin.tab.email;

import com.eltiland.model.subscribe.Email;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Email view panel.
 *
 * @author Aleksey Plotnikov.
 */
public class EmailViewPanel extends ELTDialogPanel {

    private Label headerField = new Label("headerLabel", new Model<String>());

    private MultiLineLabel contentField = new MultiLineLabel(
            "contentField", new Model<String>());

    protected EmailViewPanel(String id) {
        super(id);

        form.add(headerField);
        form.add(contentField.setEscapeModelStrings(false));
    }

    @Override
    protected String getHeader() {
        return getString("viewEmailHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        //
    }

    public void initData(IModel<Email> emailIModel) {
        contentField.setDefaultModelObject(emailIModel.getObject().getContent());
        headerField.setDefaultModelObject(String.format(getString("headerLabel"), emailIModel.getObject().getHeader()));
    }
}
