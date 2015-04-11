package com.eltiland.ui.google.pages;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page for view google document.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleDocViewPage extends AbstractGooglePage {

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/docView";

    /**
     * Page constructor.
     *
     * @param parameters page params.
     */
    public GoogleDocViewPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public ELTGoogleDriveEditor.MODE getMode() {
        return ELTGoogleDriveEditor.MODE.VIEW;
    }

    @Override
    public GoogleDriveFile.TYPE getType() {
        return GoogleDriveFile.TYPE.DOCUMENT;
    }
}
