package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.utils.MimeType;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for edit Google Drive document.
 *
 * @author Aleksey Plotnikov.
 */
abstract class GoogleDocumentEditor extends GoogleEditor {

    protected GoogleDocumentEditor(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);
    }

    @Override
    protected String getPrefix() {
        return "document";
    }

    @Override
    protected List<String> getTypes() {
        return MimeType.getDocumentTypes();
    }
}
