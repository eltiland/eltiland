package com.eltiland.ui.course.control.data.panel.item;

import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Item name/rename panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ItemPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<ELTCourseItem>, IDialogUpdateCallback<ELTCourseItem> {

    private IDialogNewCallback.IDialogActionProcessor<ELTCourseItem> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<ELTCourseItem> updateCallback;

    private ELTTextField<String> nameField =
            new ELTTextField<String>(
                    "name", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class, true) {
                @Override
                protected int getInitialWidth() {
                    return 380;
                }
            };

    private IModel<ELTCourseItem> itemModel = new GenericDBModel<>(ELTCourseItem.class);

    private boolean editMode = false;

    public ItemPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.setMultiPart(true);
    }

    @Override
    protected String getHeader() {
        return getString(!editMode ? "create.header" : "rename.header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        if (event.equals(EVENT.Save)) {
            return editMode;
        } else {
            return !editMode;
        }
    }

    public void initData(IModel<ELTCourseItem> itemModel) {
        this.itemModel = itemModel;
        nameField.setModelObject(this.itemModel.getObject().getName());
        editMode = this.itemModel.getObject().getName() != null;
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        itemModel.getObject().setName(nameField.getModelObject());
        switch (event) {
            case Create:
                newCallback.process(itemModel, target);
                break;
            case Save:
                updateCallback.process(itemModel, target);
                break;
        }
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<ELTCourseItem> callback) {
        updateCallback = callback;
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<ELTCourseItem> callback) {
        newCallback = callback;
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
