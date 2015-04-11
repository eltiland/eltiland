package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * Panel for output (read-only mode) Google Drive PDF document.
 *
 * @author Aleksey Plotnikov.
 */
public class GooglePDFViewer extends BaseEltilandPanel<GoogleDriveFile> {
    /**
     * Panel constructor.
     *
     * @param id                    markup id.
     * @param googleDriveFileIModel google drive file model.
     */
    public GooglePDFViewer(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        WebMarkupContainer content = new WebMarkupContainer("content");

        content.add(new AttributeModifier("src",
                "https://docs.google.com/file/d/" + getModelObject().getGoogleId() + "/preview"));

        add(content);
    }
}
