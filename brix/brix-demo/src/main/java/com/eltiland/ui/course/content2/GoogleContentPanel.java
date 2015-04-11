package com.eltiland.ui.course.content2;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.google.buttons.GooglePrintButton;
import com.eltiland.utils.MimeType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output simple document of course.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleContentPanel extends AbstractCourseContentPanel<ELTGoogleCourseItem> {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constructor.
     *
     * @param id                     markup id.
     * @param googleCourseItemIModel lecture model.
     */
    public GoogleContentPanel(String id, IModel<ELTGoogleCourseItem> googleCourseItemIModel) {
        super(id, googleCourseItemIModel);

        genericManager.initialize(getModelObject(), getModelObject().getItem());

        GoogleDriveFile.TYPE type = GoogleDriveFile.TYPE.DOCUMENT;
        String mimeType = getModelObject().getItem().getMimeType();
        if (MimeType.getDocumentTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.DOCUMENT;
        } else if (MimeType.getPresentationTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.PRESENTATION;
        }

        add(new ActionPanel("controlPanel",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem())) {
            @Override
            public boolean isVisible() {
                ELTGoogleCourseItem item = GoogleContentPanel.this.getModelObject();
                return (item instanceof ELTDocumentCourseItem) && ((ELTDocumentCourseItem) item).isPrintable();
            }
        });

        add(new ELTGoogleDriveEditor("contentField",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem()),
                ELTGoogleDriveEditor.MODE.VIEW, type));
    }

    private class ActionPanel extends BaseEltilandPanel<GoogleDriveFile> {

        protected ActionPanel(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
            super(id, googleDriveFileIModel);

            add(new GooglePrintButton("printButton", new GenericDBModel<>(GoogleDriveFile.class, getModelObject())));
        }
    }
}
