package com.eltiland.ui.library.panels.type;

import com.eltiland.model.library.LibraryDocumentRecord;
import com.eltiland.model.library.LibraryPresentationRecord;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.utils.MimeType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for creating/editing image entity of the library.
 *
 * @author Aleksey Plotnikov.
 */
public class DocumentPropertyPanel extends AbstractContentPropertyPanel<LibraryRecord> {

    private boolean document;

    public DocumentPropertyPanel(String id, boolean document) {
        super(id);
        this.document = document;
    }

    public DocumentPropertyPanel(String id, IModel<LibraryRecord> libraryRecordIModel, boolean document) {
        super(id, libraryRecordIModel);
        this.document = document;
    }

    @Override
    protected List<String> getAvailibleMimeTypes() {
        if (document) {
            return new ArrayList<>(MimeType.getDocumentTypes());
        } else {
            return new ArrayList<>(MimeType.getPresentationTypes());
        }
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
    }

    @Override
    protected Class<? extends LibraryRecord> getItemClass() {
        return document ? LibraryDocumentRecord.class : LibraryPresentationRecord.class;
    }
}
