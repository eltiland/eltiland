package com.eltiland.ui.library.panels.type;

import com.eltiland.model.library.LibraryArchiveRecord;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.utils.MimeType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Panel for creating/editing archive entity of the library.
 *
 * @author Aleksey Plotnikov.
 */
public class ArchivePropertyPanel extends AbstractContentPropertyPanel<LibraryArchiveRecord> {
    public ArchivePropertyPanel(String id) {
        super(id);
    }

    public ArchivePropertyPanel(String id, IModel<LibraryArchiveRecord> libraryRecordIModel) {
        super(id, libraryRecordIModel);
    }

    @Override
    protected List<String> getAvailibleMimeTypes() {
        return MimeType.getArchiveTypes();
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
        return LibraryArchiveRecord.class;
    }
}
