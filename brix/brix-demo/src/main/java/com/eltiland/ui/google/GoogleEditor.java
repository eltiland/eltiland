package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.google.buttons.GoogleUploadButton;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.List;

/**
 * Panel for edit Google Drive entity.
 *
 * @author Aleksey Plotnikov.
 */
abstract class GoogleEditor extends BaseEltilandPanel<GoogleDriveFile> {
    protected GoogleEditor(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        final WebMarkupContainer editor = new WebMarkupContainer("editor");

        GoogleUploadButton uploadButton = new GoogleUploadButton("uploadButton") {
            @Override
            public void onClick(GoogleDriveFile gFile) {
                GoogleEditor.this.setModelObject(gFile);
                editor.add(new AttributeModifier("src",
                        "https://docs.google.com/" + getPrefix() + "/d/" + gFile.getGoogleId() + "/edit?hl=en_GB"));
                onUpload(gFile);
            }

            @Override
            public List<String> getAvailibleMimeTypes() {
                return getTypes();
            }
        };

        uploadButton.add(new AttributeModifier("title", new ResourceModel("upload")));
        uploadButton.add(new TooltipBehavior());

        editor.add(new AttributeAppender("src",
                "https://docs.google.com/" + getPrefix() + "/d/" + getModelObject().getGoogleId() + "/edit?hl=en_GB"));

        add(getAdditionalPanel("additionalPanel"));
        add(editor);
        add(uploadButton);
    }

    protected abstract void onUpload(GoogleDriveFile file);

    protected abstract String getPrefix();

    protected abstract List<String> getTypes();

    protected abstract Panel getAdditionalPanel(String markupId);

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
    }
}
