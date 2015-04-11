package com.eltiland.ui.google.pages;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page for edit google document.
 *
 * @author Aleksey Plotnikov.
 */
public class GooglePresEditPage extends AbstractGooglePage {

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/presEdit";

    /**
     * Page constructor.
     *
     * @param parameters page params.
     */
    public GooglePresEditPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public ELTGoogleDriveEditor.MODE getMode() {
        return ELTGoogleDriveEditor.MODE.EDIT;
    }

    @Override
    public GoogleDriveFile.TYPE getType() {
        return GoogleDriveFile.TYPE.PRESENTATION;
    }
}
