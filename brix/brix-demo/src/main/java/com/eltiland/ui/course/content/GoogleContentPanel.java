package com.eltiland.ui.course.content;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.DocumentCourseItem;
import com.eltiland.model.course.GoogleCourseItem;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.google.buttons.GooglePrintButton;
import com.eltiland.utils.MimeType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output simple document of course.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleContentPanel extends CourseContentPanel<GoogleCourseItem> {

    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constructor.
     *
     * @param id                     markup id.
     * @param googleCourseItemIModel lecture model.
     */
    public GoogleContentPanel(String id, IModel<GoogleCourseItem> googleCourseItemIModel) {
        super(id, googleCourseItemIModel);
    }

    @Override
    protected WebMarkupContainer getContent() {
        WebMarkupContainer content = new WebMarkupContainer("content");

        genericManager.initialize(getModelObject(), getModelObject().getDriveFile());
        GoogleDriveFile.TYPE type = GoogleDriveFile.TYPE.DOCUMENT;
        String mimeType = getModelObject().getDriveFile().getMimeType();
        if (MimeType.getDocumentTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.DOCUMENT;
        } else if (MimeType.getPresentationTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.PRESENTATION;
        }

        content.add(new ActionPanel("controlPanel",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getDriveFile())) {
            @Override
            public boolean isVisible() {
                GoogleCourseItem item = GoogleContentPanel.this.getModelObject();
                return (item instanceof DocumentCourseItem) && ((DocumentCourseItem) item).isPrintable();
            }
        });

        content.add(new ELTGoogleDriveEditor("contentField",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getDriveFile()),
                ELTGoogleDriveEditor.MODE.VIEW, type));
        return content;
    }

    private class ActionPanel extends BaseEltilandPanel<GoogleDriveFile> {

        protected ActionPanel(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
            super(id, googleDriveFileIModel);

            add(new GooglePrintButton("printButton", new GenericDBModel<>(GoogleDriveFile.class, getModelObject())));
        }
    }
}
