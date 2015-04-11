package com.eltiland.ui.library.panels.type;

import com.eltiland.model.library.LibraryImageRecord;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.utils.MimeType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for creating/editing image entity of the library.
 *
 * @author Aleksey Plotnikov.
 */
public class ImagePropertyPanel extends AbstractContentPropertyPanel<LibraryImageRecord> {
    public ImagePropertyPanel(String id) {
        super(id);
    }

    public ImagePropertyPanel(String id, IModel<LibraryImageRecord> libraryRecordIModel) {
        super(id, libraryRecordIModel);
    }

    @Override
    protected List<String> getAvailibleMimeTypes() {
        return MimeType.getImageTypes();
    }

    @Override
    protected WebMarkupContainer getAdditionalPanel() {
        WebMarkupContainer loadedContainer = new WebMarkupContainer("loadedContainer") {
            @Override
            public boolean isVisible() {
                return fileIModel.getObject() != null;
            }
        };
        return loadedContainer;

//        IconButton previewButton = new IconButton(
//                "additionalPanel", new ResourceModel("preview"), ButtonAction.PREVIEW) {
//            @Override
//            public boolean isVisible() {
//                return fileIModel.getObject() != null;
//            }
//
//            @Override
//            protected void onClick(AjaxRequestTarget target) {
//                throw new RestartResponseException(ImagePreviewPage.class,
//                        new PageParameters().add(ImagePreviewPage.PARAM_ID, fileIModel.getObject().getId()));
//            }
//        };
//        return previewButton;
       // return new EmptyPanel("additionalPanel");
    }

    @Override
    protected Class<? extends LibraryRecord> getItemClass() {
        return LibraryImageRecord.class;
    }
}
