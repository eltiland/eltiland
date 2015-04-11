package com.eltiland.ui.course.edit.video;

import com.eltiland.model.course2.content.video.ELTVideoItem;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.components.video.YoutubeLinkVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract panel for editing course item.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTVideoItem>,
        IDialogNewCallback<ELTVideoItem> {

    private static final int MAX_LEN = 1024;

    private boolean editMode;

    private IDialogUpdateCallback.IDialogActionProcessor<ELTVideoItem> callback;
    private IDialogNewCallback.IDialogActionProcessor<ELTVideoItem> newCallback;

    private IModel<ELTVideoItem> itemModel = new GenericDBModel<>(ELTVideoItem.class);

    private ELTTextField<String> nameField = new ELTTextField<String>(
            "name", new ResourceModel("nameLabel"), new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 630;
        }
    };

    private YoutubeLinkVideoPlayer player = new YoutubeLinkVideoPlayer("video", new Model<String>()) {
        @Override
        protected boolean isRequiredField() {
            return true;
        }

        @Override
        protected int getInitialWidth() {
            return 630;
        }
    };

    public VideoPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(player);
    }

    public void initData(IModel<ELTVideoItem> itemModel) {
        this.itemModel = itemModel;
        nameField.setModelObject(this.itemModel.getObject().getName());
        player.setModelObject(this.itemModel.getObject().getLink());
        editMode = true;
    }

    public void initCreate() {
        nameField.setModelObject(null);
        player.setModelObject(null);
        editMode = false;
    }

    @Override
    protected String getHeader() {
        return getString("headerVideo");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save, EVENT.Create));
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        return event.equals(EVENT.Create) && !editMode || event.equals(EVENT.Save) && editMode;
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            itemModel.getObject().setName(nameField.getModelObject());
            itemModel.getObject().setLink(player.getModelObject());
            callback.process(itemModel, target);
        } else if (event.equals(EVENT.Create)) {
            ELTVideoItem item = new ELTVideoItem();
            item.setName(nameField.getModelObject());
            item.setLink(player.getModelObject());
            newCallback.process(new GenericDBModel<>(ELTVideoItem.class, item), target);
        }

    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<ELTVideoItem> callback) {
        this.callback = callback;
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<ELTVideoItem> callback) {
        this.newCallback = callback;
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
