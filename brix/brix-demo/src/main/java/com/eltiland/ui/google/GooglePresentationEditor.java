package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.utils.MimeType;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for edit Google Drive presentation.
 *
 * @author Aleksey Plotnikov.
 */
abstract class GooglePresentationEditor extends GoogleEditor {

    protected GooglePresentationEditor(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);
    }

    @Override
    protected String getSource(GoogleDriveFile gFile) {
        return "https://docs.google.com/" + getPrefix() + "/d/" + gFile.getGoogleId() + "/edit?hl=en_GB";
    }

    @Override
    protected String getPrefix() {
        return "presentation";
    }

    @Override
    protected List<String> getTypes() {
        return MimeType.getPresentationTypes();
    }
}
