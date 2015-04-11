package com.eltiland.ui.course.edit.video;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.model.course2.content.video.ELTVideoItem;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for editing description of the video.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoDescriptionPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTVideoItem> {

    private static final int MAX_LEN = 2048;

    private IDialogActionProcessor<ELTVideoItem> callback;

    private IModel<ELTVideoItem> itemModel = new GenericDBModel<>(ELTVideoItem.class);

    private ELTTextArea descriptionField =
            new ELTTextArea("description", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
                @Override
                protected boolean isFillToWidth() {
                    return true;
                }
            };

    public VideoDescriptionPanel(String id) {
        super(id);
        form.add(descriptionField);
        descriptionField.addMaxLengthValidator(MAX_LEN);
        descriptionField.registerEditorBehaviour(new TinyMceEnabler());
    }

    public void initData(IModel<ELTVideoItem> itemModel) {
        this.itemModel = itemModel;
        descriptionField.setModelObject(this.itemModel.getObject().getDescription());
    }

    @Override
    protected String getHeader() {
        return getString("headerDesc");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            itemModel.getObject().setDescription(descriptionField.getModelObject());
            callback.process(itemModel, target);
        }
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<ELTVideoItem> callback) {
        this.callback = callback;
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
