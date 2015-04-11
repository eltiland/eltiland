package com.eltiland.ui.tags.plugin.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
 * Tag category creating panel.
 *
 * @author Aleksey Plotnikov.
 */
public class TagCategoryPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<TagCategory>, IDialogUpdateCallback<TagCategory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagCategoryPropertyPanel.class);

    @SpringBean
    private TagCategoryManager tagCategoryManager;
    @SpringBean
    private GenericManager genericManager;

    private IDialogNewCallback.IDialogActionProcessor<TagCategory> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<TagCategory> updateCallback;
    private Class<? extends ITagable> clazz;
    private boolean mode;

    private ELTTextField nameField =
            new ELTTextField<>("nameField", new ResourceModel("name"), new Model<String>(), String.class, true);

    private IModel<TagCategory> categoryModel = new GenericDBModel<>(TagCategory.class);

    public TagCategoryPropertyPanel(String id, Class<? extends ITagable> clazz) {
        super(id);
        this.clazz = clazz;
        form.add(nameField);
    }

    public void initEditMode(TagCategory entity) {
        categoryModel.setObject(entity);
        nameField.setModelObject(entity.getName());
        mode = true;
    }

    public void initCreateMode() {
        mode = false;
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
        if (event.equals(EVENT.Create)) {
            TagCategory category = new TagCategory();
            category.setEntity(clazz.getSimpleName());
            category.setName((String) nameField.getModelObject());
            try {
                tagCategoryManager.createTagCategory(category);
            } catch (EltilandManagerException e) {
                LOGGER.error("Got exception when creating tag entity", e);
                throw new WicketRuntimeException("Got exception when creating tag category entity", e);
            }

            newCallback.process(new GenericDBModel<>(TagCategory.class, category), target);
        } else if (event.equals(EVENT.Save)) {
            TagCategory category = categoryModel.getObject();
            category.setName((String) nameField.getModelObject());
            try {
                genericManager.update(category);
            } catch (ConstraintException e) {
                LOGGER.error("Got exception when update tag entity", e);
                throw new WicketRuntimeException("Got exception when updating tag category entity", e);
            }
            updateCallback.process(new GenericDBModel<>(TagCategory.class, category), target);
        }
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<TagCategory> callback) {
        this.newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<TagCategory> callback) {
        this.updateCallback = callback;
    }
}
