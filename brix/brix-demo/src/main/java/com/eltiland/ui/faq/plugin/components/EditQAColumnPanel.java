package com.eltiland.ui.faq.plugin.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Panel for editing QA.
 *
 * @author Aleksey PLotnikov.
 */
public abstract class EditQAColumnPanel extends BaseEltilandPanel<String> {

    private Dialog<EditQAPanel> editQAPanelDialog = new Dialog<EditQAPanel>("editQADialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public EditQAPanel createDialogPanel(String id) {
            return new EditQAPanel(id);
        }

        @Override
        public void registerCallback(EditQAPanel panel) {
            super.registerCallback(panel);

            panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    close(target);
                    onChange(model.getObject(), target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           panel's ID.
     * @param stringIModel QA string model.
     */
    public EditQAColumnPanel(String id, IModel<String> stringIModel) {
        super(id, stringIModel);

        add(new Label("qaLabel", stringIModel.getObject()));

        EltiAjaxLink editLink = new EltiAjaxLink("editLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                editQAPanelDialog.show(target);
            }
        };

        add(editLink);
        editLink.add(new WebMarkupContainer("editImage"));

        add(editQAPanelDialog);
    }

    public abstract void onChange(String newValue, AjaxRequestTarget target);
}
