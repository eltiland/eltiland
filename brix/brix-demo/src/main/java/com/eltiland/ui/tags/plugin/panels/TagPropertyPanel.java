package com.eltiland.ui.tags.plugin.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating/editing tag.
 *
 * @author Aleksey Plotnikov.
 */
public class TagPropertyPanel extends ELTDialogPanel implements IDialogNewCallback<Tag>, IDialogUpdateCallback<Tag> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagPropertyPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TagManager tagManager;


    private IDialogNewCallback.IDialogActionProcessor<Tag> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<Tag> updateCallback;
    private boolean mode;
    private IModel<Tag> tagModel = new GenericDBModel<>(Tag.class);
    private IModel<TagCategory> tagCategoryModel = new GenericDBModel<>(TagCategory.class);

    private ELTTextField nameField =
            new ELTTextField<>("nameField", new ResourceModel("name"), new Model<String>(), String.class, true);

    public TagPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(new AbstractFormValidator() {
            @Override
            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{nameField};
            }

            @Override
            public void validate(Form components) {
                String name = (String) nameField.getConvertedInput();
                if (tagManager.checkTagExists(tagCategoryModel.getObject(), name)) {
                    this.error(nameField, "tagExistsError");
                }
            }
        });
    }

    public void initCreateMode(TagCategory category) {
        mode = false;
        tagCategoryModel.setObject(category);
    }

    public void initEditMode(Tag entity) {
        tagModel.setObject(entity);
        nameField.setModelObject(entity.getName());
        mode = true;
    }

    @Override
    protected String getHeader() {
        return getString(mode ? "titleEdit" : "titleCreate");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save, EVENT.Create));
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        if (event.equals(EVENT.Create)) {
            return !mode;
        } else {
            return mode;
        }
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            Tag tag = tagModel.getObject();
            tag.setName((String) nameField.getModelObject());

            try {
                genericManager.update(tag);
            } catch (ConstraintException e) {
                LOGGER.error("Got exception when updating tag entity", e);
                throw new WicketRuntimeException("Got exception when updating tag entity", e);
            }

            updateCallback.process(new GenericDBModel<>(Tag.class, tag), target);
        } else if (event.equals(EVENT.Create)) {
            Tag tag = new Tag();
            tag.setCategory(tagCategoryModel.getObject());
            tag.setName((String) nameField.getModelObject());

            try {
                genericManager.saveNew(tag);
            } catch (ConstraintException e) {
                LOGGER.error("Got exception when creating tag entity", e);
                throw new WicketRuntimeException("Got exception when creating tag entity", e);
            }

            newCallback.process(new GenericDBModel<>(Tag.class, tag), target);
        }
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<Tag> callback) {
        newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<Tag> callback) {
        updateCallback = callback;
    }
}
