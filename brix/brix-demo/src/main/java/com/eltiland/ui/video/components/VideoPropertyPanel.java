package com.eltiland.ui.video.components;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.Video;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.image.ImageColumn;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.label.multiselect.AjaxMultiSelector;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.components.video.YoutubeLinkVideoPlayer;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for creating new video.
 */
public class VideoPropertyPanel extends BaseEltilandPanel<Video>
        implements IDialogNewCallback<Video>, IDialogUpdateCallback<Video> {

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private GenericManager genericManager;

    private IDialogNewCallback.IDialogActionProcessor<Video> callback;
    private IDialogUpdateCallback.IDialogActionProcessor<Video> updateCallback;

    private static final int MAX_LEN = 1024;

    private ELTTextField<String> nameField = new ELTTextField<>(
            "videoName", new ResourceModel("nameLabel"), new Model<String>(), String.class, true);

    private YoutubeLinkVideoPlayer player = new YoutubeLinkVideoPlayer("video", new Model<String>()) {
        @Override
        protected boolean isRequiredField() {
            return true;
        }
    };

    private ELTTextArea descriptionField =
            new ELTTextArea("description", new ResourceModel("descriptionLabel"), new Model<String>()) {
                @Override
                protected boolean isFillToWidth() {
                    return true;
                }
            };

    /*
   User selector
    */
    private final AjaxMultiSelector<User> userSelector = new AjaxMultiSelector<User>(
            "authorSelector",
            new ResourceModel("authorLabel"),
            new GenericDBListModel<>(User.class),
            new ResourceModel("selectAuthor"),
            true) {

        @Override
        public Component getChosenElementComponent(String id, User element) {
            return new Label(id, element.getName());
        }

        @Override
        protected List<IColumn<User>> createColumns() {
            return Arrays.<IColumn<User>>asList(
                    new ImageColumn<User>(),
                    new PropertyColumn(new ResourceModel("name"), "id", "name")
            );
        }

        @Override
        protected ISortableDataProvider<User> createDataProvider() {
            return new EltiDataProviderBase<User>() {
                @Override
                public Iterator<User> iterator(int first, int count) {
                    return userManager.getUserSearchList(
                            first, count, getSearchQueryModel().getObject(), "name", false).iterator();
                }

                @Override
                public int size() {
                    return userManager.getUserSearchCount(getSearchQueryModel().getObject());
                }
            };
        }

        @Override
        protected String getSelectLinkText() {
            return getString("select");
        }
    };

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            Video video = new Video();
            video = fillEntity(video);

            callback.process(new GenericDBModel(Video.class, video), target);
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            Video video = (Video) VideoPropertyPanel.this.getDefaultModelObject();

            video = fillEntity(video);
            updateCallback.process(new GenericDBModel<>(Video.class, video), target);
        }
    };


    private Label headerLabel = new Label("header", new Model<String>());

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public VideoPropertyPanel(String id) {
        super(id);

        addComponents();

        nameField.setModelObject(null);
        descriptionField.setModelObject(null);
        player.setModelObject(null);

        headerLabel.setDefaultModelObject(getString("header"));
        createButton.setVisible(true);
        saveButton.setVisible(false);
    }

    /**
     * Panel constructor.
     *
     * @param id          markup id.
     * @param videoIModel video model.
     */
    public VideoPropertyPanel(String id, IModel<Video> videoIModel) {
        super(id, videoIModel);

        addComponents();

        nameField.setModelObject(videoIModel.getObject().getName());
        descriptionField.setModelObject(videoIModel.getObject().getDescription());
        player.setModelObject(videoIModel.getObject().getLink());

        genericManager.initialize(videoIModel.getObject(), videoIModel.getObject().getAuthor());
        if (videoIModel.getObject().getAuthor() != null) {
            userSelector.setModelObject(new ArrayList<>(Arrays.asList(videoIModel.getObject().getAuthor())));
        }

        headerLabel.setDefaultModelObject(getString("headerSave"));
        createButton.setVisible(false);
        saveButton.setVisible(true);
    }

    private void addComponents() {
        add(headerLabel);

        Form form = new Form("form");

        form.add(createButton);
        form.add(saveButton);

        form.add(nameField);
        form.add(descriptionField);
        form.add(player);
        form.add(userSelector);

        nameField.addMaxLengthValidator(MAX_LEN);
        descriptionField.addMaxLengthValidator(MAX_LEN);
        descriptionField.registerEditorBehaviour(new TinyMceEnabler());

        add(form);

        form.add(new FormRequired("required"));
    }

    private Video fillEntity(Video video) {
        List<User> users = userSelector.getModelObject();

        video.setAuthor(users.isEmpty() ? null : users.get(0));
        video.setDescription(descriptionField.getModelObject());
        video.setLink((String) player.getDefaultModelObject());
        video.setName(nameField.getModelObject());

        return video;
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<Video> callback) {
        this.callback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<Video> callback) {
        this.updateCallback = callback;
    }
}
