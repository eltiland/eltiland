package com.eltiland.ui.forum.panels;

import com.eltiland.bl.forum.ForumManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
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
 * Panel for creating/editing forums.
 *
 * @author Aleksey Plotnikov
 */
public class ForumPropertyPanel extends BaseEltilandPanel<Forum> implements IDialogNewCallback<Forum> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumPropertyPanel.class);

    @SpringBean
    private ForumManager forumManager;

    private IDialogActionProcessor<Forum> newCallback;

    private Label headerLabel = new Label("header", new Model<String>());

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            Forum forum = new Forum();
            forum.setName(nameField.getModelObject());
            forum.setDescription(descriptionField.getModelObject());
            forum.setForumgroup(forumGroupModel.getObject());
            try {
                forumManager.createForum(forum);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot create forum", e);
                throw new WicketRuntimeException("Cannot create forum", e);
            }
            newCallback.process(new GenericDBModel<>(Forum.class, forum), target);
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
        }
    };

    private ELTTextArea nameField = new ELTTextArea(
            "nameField", new ResourceModel("nameField"), new Model<String>(), true);

    private ELTTextArea descriptionField = new ELTTextArea(
            "descriptionField", new ResourceModel("descriptionField"), new Model<String>());

    private IModel<ForumGroup> forumGroupModel = new GenericDBModel<>(ForumGroup.class);

    public ForumPropertyPanel(String id, IModel<ForumGroup> forumGroupIModel) {
        super(id, new GenericDBModel<>(Forum.class));

        forumGroupModel.setObject(forumGroupIModel.getObject());

        add(headerLabel);
        Form form = new Form("form");
        add(form);

        form.add(nameField);
        form.add(descriptionField);
        form.add(new FormRequired("required"));

        form.add(createButton);
        form.add(saveButton);

        initCreateMode();
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<Forum> callback) {
        newCallback = callback;
    }

    public void initEditMode(Forum forum) {
        setModelObject(forum);
        headerLabel.setDefaultModelObject(getString("editHeader"));
        createButton.setVisible(false);
        saveButton.setVisible(true);
    }

    public void initCreateMode() {
        headerLabel.setDefaultModelObject(getString("createHeader"));
        createButton.setVisible(true);
        saveButton.setVisible(false);
    }
}
