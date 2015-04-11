package com.eltiland.ui.forum.panels;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.forum.ForumThreadManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for creating and editing forum threads.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumThreadPropertyPanel extends BaseEltilandPanel<ForumThread>
        implements IDialogNewCallback<ForumThread> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumThreadPropertyPanel.class);

    @SpringBean
    private ForumThreadManager forumThreadManager;

    private IDialogActionProcessor<ForumThread> newCallback;

    private ELTTextArea nameField = new ELTTextArea(
            "nameField", new ResourceModel("nameField"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    private CKEditorFull firstMessageText;

    private Label headerLabel = new Label("header", new Model<String>());


    public ForumThreadPropertyPanel(String id, final IModel<Forum> forumModel, Dialog dialog) {
        super(id);

        add(headerLabel);

        Form form = new Form("form");
        add(form);

        form.add(nameField);
        firstMessageText = new CKEditorFull("firstMessageText", dialog);
        form.add(firstMessageText);

        form.add(new EltiAjaxSubmitLink("createButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ForumThread thread = new ForumThread();
                thread.setName(nameField.getModelObject());
                thread.setForum(forumModel.getObject());

                try {
                    forumThreadManager.createThread(thread, firstMessageText.getData());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create forum thread", e);
                    throw new WicketRuntimeException("Cannot create forum thread", e);
                }

                newCallback.process(new GenericDBModel<>(ForumThread.class, thread), target);
            }
        });

        form.add(new FormRequired("required"));
        initCreateMode();
    }

    public void initCreateMode() {
        headerLabel.setDefaultModelObject(getString("createHeader"));
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<ForumThread> callback) {
        newCallback = callback;
    }
}
