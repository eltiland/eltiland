package com.eltiland.ui.forum.panels.message;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Forum create panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ForumCreateMessagePanel extends BaseEltilandPanel<ForumMessage> {
    private ELTTextArea nameField = new ELTTextArea(
            "nameField", new ResourceModel("nameField"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    private CKEditorFull messageText;

    public ForumCreateMessagePanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);

        form.add(nameField);
        messageText = new CKEditorFull("messageText", null);
        form.add(messageText);
        form.add(new FormRequired("required"));

        form.add(new EltiAjaxSubmitLink("createButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form components) {
                ForumMessage message = new ForumMessage();
                message.setHeader(nameField.getModelObject());
                message.setContent(messageText.getData());
                onCreate(message, ajaxRequestTarget);
            }
        });

        form.add(new EltiAjaxLink("closeButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                onClose(ajaxRequestTarget);
            }
        });
    }

    protected abstract void onClose(AjaxRequestTarget target);

    protected abstract void onCreate(ForumMessage message, AjaxRequestTarget target);
}
