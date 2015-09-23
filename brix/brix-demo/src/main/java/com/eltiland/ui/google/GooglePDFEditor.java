package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.utils.MimeType;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for edit Google Drive PDF file.
 *
 * @author Aleksey Plotnikov.
 */
abstract class GooglePDFEditor extends GoogleEditor {

    protected GooglePDFEditor(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);
    }

    @Override
    protected String getSource(GoogleDriveFile gFile) {
        return "https://docs.google.com/file/d/" + gFile.getGoogleId() + "/preview";
    }

    @Override
    protected String getPrefix() {
        return StringUtils.EMPTY;
    }

    @Override
    protected List<String> getTypes() {
        return MimeType.getPDFTypes();
    }
}
