package com.eltiland.ui.google;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.utils.MimeType;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Data editor of google drive file.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTGoogleDriveEditor extends BaseEltilandPanel<GoogleDriveFile> {

    public enum MODE {VIEW, EDIT}

    public ELTGoogleDriveEditor(String id, IModel<GoogleDriveFile> googleDriveFileIModel,
                                MODE mode, GoogleDriveFile.TYPE type) {
        super(id, googleDriveFileIModel);

        if (mode.equals(MODE.VIEW)) {
            if (type.equals(GoogleDriveFile.TYPE.DOCUMENT)) {
                if (getModelObject().getMimeType().equals(MimeType.PDF_TYPE)) {
                    add(new GooglePDFViewer("editor", getModel()));
                } else {
                    add(new GoogleDocumentViewer("editor", getModel()));
                }
            } else if (type.equals(GoogleDriveFile.TYPE.PRESENTATION)) {
                add(new GooglePresentationViewer("editor", getModel()));
            }
        } else if (mode.equals(MODE.EDIT)) {
            if (type.equals(GoogleDriveFile.TYPE.DOCUMENT)) {
                add(new GoogleDocumentEditor("editor", getModel()) {
                    @Override
                    protected void onUpload(GoogleDriveFile file) {
                        ELTGoogleDriveEditor.this.onUpload(file);
                    }

                    @Override
                    protected Panel getAdditionalPanel(String markupId) {
                        return ELTGoogleDriveEditor.this.getAdditionalPanel(markupId);
                    }
                });
            } else if (type.equals(GoogleDriveFile.TYPE.PRESENTATION)) {
                add(new GooglePresentationEditor("editor", getModel()) {
                    @Override
                    protected void onUpload(GoogleDriveFile file) {
                        ELTGoogleDriveEditor.this.onUpload(file);
                    }

                    @Override
                    protected Panel getAdditionalPanel(String markupId) {
                        return ELTGoogleDriveEditor.this.getAdditionalPanel(markupId);
                    }
                });
            }
        }
    }

    protected void onUpload(GoogleDriveFile file) {

    }

    protected Panel getAdditionalPanel(String markupId) {
        return new EmptyPanel(markupId);
    }
}
