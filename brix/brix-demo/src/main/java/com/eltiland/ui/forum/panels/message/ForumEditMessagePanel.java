package com.eltiland.ui.forum.panels.message;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for editing forum message.
 *
 * @author Aleksey Pltonikov.
 */
public class ForumEditMessagePanel extends BaseEltilandPanel<ForumMessage>
        implements IDialogUpdateCallback<ForumMessage> {

    @SpringBean
    private ForumMessageManager forumMessageManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumEditMessagePanel.class);

    private IDialogActionProcessor<ForumMessage> updateCallback;

    private ELTTextArea nameField = new ELTTextArea(
            "nameField", new ResourceModel("nameField"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    private CKEditorFull messageText;

    protected ForumEditMessagePanel(String id, Dialog dialog) {
        super(id, new GenericDBModel<>(ForumMessage.class));

        Form form = new Form("form");
        form.add(nameField);
        messageText = new CKEditorFull("messageText", dialog);
        form.add(messageText);
        form.add(new FormRequired("required"));

        add(form);

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                ForumMessage message = ForumEditMessagePanel.this.getModelObject();
                message.setContent(messageText.getData());
                message.setHeader(nameField.getModelObject());

                try {
                    forumMessageManager.updateForumMessage(message);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update Message", e);
                    throw new WicketRuntimeException("Cannot update Message", e);
                }

                updateCallback.process(new GenericDBModel<>(ForumMessage.class, message), target);
            }
        });
    }

    public void initEditMode(ForumMessage message) {
        nameField.setModelObject(message.getHeader());
        messageText.setData(message.getContent());
        setModelObject(message);
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<ForumMessage> callback) {
        this.updateCallback = callback;
    }
}
