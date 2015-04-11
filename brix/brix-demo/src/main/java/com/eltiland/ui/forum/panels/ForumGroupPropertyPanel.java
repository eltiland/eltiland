package com.eltiland.ui.forum.panels;

import com.eltiland.bl.forum.ForumGroupManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.model.forum.GeneralForumGroup;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for creating/editing forum group.
 *
 * @author Aleksey Plotnikov
 */
public class ForumGroupPropertyPanel extends BaseEltilandPanel<ForumGroup>
        implements IDialogNewCallback<ForumGroup>, IDialogUpdateCallback<ForumGroup> {

    @SpringBean
    private ForumGroupManager forumGroupManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumGroupPropertyPanel.class);

    private Label headerLabel = new Label("header", new Model<String>());

    private IDialogNewCallback.IDialogActionProcessor<ForumGroup> createCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<ForumGroup> updateCallback;

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            ForumGroup forumGroup = new GeneralForumGroup();
            forumGroup.setName(nameField.getModelObject());
            try {
                forumGroupManager.createForumGroup(forumGroup);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot create forum group", e);
                throw new WicketRuntimeException("Cannot create forum group", e);
            }
            createCallback.process(new GenericDBModel<>(ForumGroup.class, forumGroup), target);
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            ForumGroup forumGroup = ForumGroupPropertyPanel.this.getModelObject();
            forumGroup.setName(nameField.getModelObject());

            try {
                forumGroupManager.updateForumGroup(forumGroup);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot update forum group", e);
                throw new WicketRuntimeException("Cannot update forum group", e);
            }
            updateCallback.process(new GenericDBModel<>(ForumGroup.class, forumGroup), target);
        }
    };

    private ELTTextField<String> nameField = new ELTTextField<>(
            "nameField", new ResourceModel("groupName"), new Model<String>(), String.class, true);

    /**
     * Constructor for creating
     *
     * @author Aleksey Plotnikov
     */
    public ForumGroupPropertyPanel(String id) {
        super(id, new GenericDBModel<>(ForumGroup.class));

        add(headerLabel);

        Form form = new Form("form");
        form.add(nameField);
        form.add(createButton);
        form.add(saveButton);
        form.add(new FormRequired("required"));

        nameField.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> stringIValidatable) {
                if (forumGroupManager.getForumGroupByName(stringIValidatable.getValue()) != null) {
                    stringIValidatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource iErrorMessageSource) {
                            return getString("errorGroupExists");
                        }
                    });
                }
            }
        });

        add(form);
        initCreateMode();
    }

    public void initEditMode(ForumGroup forumGroup) {
        setModelObject(forumGroup);
        headerLabel.setDefaultModelObject(getString("editHeader"));
        nameField.setModelObject(forumGroup.getName());
        createButton.setVisible(false);
        saveButton.setVisible(true);
    }

    public void initCreateMode() {
        headerLabel.setDefaultModelObject(getString("createHeader"));
        createButton.setVisible(true);
        saveButton.setVisible(false);
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<ForumGroup> callback) {
        createCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<ForumGroup> callback) {
        updateCallback = callback;
    }
}
