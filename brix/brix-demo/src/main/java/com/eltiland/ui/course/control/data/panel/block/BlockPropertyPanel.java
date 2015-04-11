package com.eltiland.ui.course.control.data.panel.block;

import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating/renaming block of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class BlockPropertyPanel extends ELTDialogPanel
        implements IDialogSimpleNewCallback<String>, IDialogUpdateCallback<ELTCourseBlock> {

    private IModel<ELTCourseBlock> blockIModel = new GenericDBModel<>(ELTCourseBlock.class);

    private ELTTextField<String> nameField =
            new ELTTextField<String>("name", new ResourceModel("block.name"), new Model<String>(), String.class, true) {
                @Override
                protected int getInitialWidth() {
                    return 450;
                }
            };

    private boolean create = true;

    private IDialogSimpleNewCallback.IDialogActionProcessor<String> createCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<ELTCourseBlock> updateCallback;

    public BlockPropertyPanel(String id) {
        super(id);
        form.add(nameField);
    }

    public void setModeCreate() {
        create = true;
    }

    public void setModeEdit(IModel<ELTCourseBlock> blockModel) {
        create = false;
        blockIModel = blockModel;
        nameField.setModelObject(blockIModel.getObject().getName());
    }

    @Override
    protected String getHeader() {
        return getString(create ? "header.create" : "header.edit");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        switch (event) {
            case Create:
                return create;
            case Save:
                return !create;
            default:
                return false;
        }
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Create)) {
            createCallback.process(new Model<>(nameField.getModelObject()), target);
        } else if (event.equals(EVENT.Save)) {
            blockIModel.getObject().setName(nameField.getModelObject());
            updateCallback.process(blockIModel, target);
        }
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    public void setSimpleNewCallback(IDialogSimpleNewCallback.IDialogActionProcessor<String> callback) {
        this.createCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<ELTCourseBlock> callback) {
        this.updateCallback = callback;
    }
}
