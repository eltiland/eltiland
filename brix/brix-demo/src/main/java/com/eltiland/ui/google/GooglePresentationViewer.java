package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * Panel for output (read-only mode) Google Drive presentation.
 *
 * @author Aleksey Plotnikov.
 */
class GooglePresentationViewer extends BaseEltilandPanel<GoogleDriveFile> {

    public GooglePresentationViewer(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        WebMarkupContainer content = new WebMarkupContainer("content");

        content.add(new AttributeModifier("src", "https://docs.google.com/presentation/d/"
                + getModelObject().getGoogleId() + "/embed?start=false&loop=false&delayms=3000"));

        add(content);
    }
}
