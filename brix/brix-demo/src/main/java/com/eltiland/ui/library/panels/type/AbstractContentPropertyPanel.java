package com.eltiland.ui.library.panels.type;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.buttons.UploadButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel for creating/editing image entity of the library.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractContentPropertyPanel<T extends LibraryRecord> extends GeneralPropertyPanel<T> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    protected IModel<File> fileIModel = new GenericDBModel<>(File.class);

    /**
     * Ctor for creating new entity.
     *
     * @param id markup id.
     */
    public AbstractContentPropertyPanel(String id) {
        super(id);
        addComponents();
    }

    /**
     * Ctor for editing new entity.
     *
     * @param id                       markup id.
     * @param libraryRecordIModel document record entity.
     */
    public AbstractContentPropertyPanel(String id, IModel<T> libraryRecordIModel) {
        super(id, libraryRecordIModel);
        fileIModel.setObject(getModelObject().getFileContent());
        addComponents();
    }

    private void addComponents() {
        add(new UploadButton("imageUpload") {
            @Override
            public List<String> getAvailibleMimeTypes() {
                return AbstractContentPropertyPanel.this.getAvailibleMimeTypes();
            }

            @Override
            protected String getButtonClass() {
                return "action center";
            }

            @Override
            protected String getInternalClass() {
                return "icon upload";
            }

            @Override
            protected Panel getPanel() {
                return new LabelPanel("panel");
            }

            @Override
            protected void onClick(File image) {
                genericManager.initialize(image, image.getBody());
                fileIModel.setObject(image);
            }
        });
        add(getAdditionalPanel());
    }

    private class LabelPanel extends Panel {
        public LabelPanel(String id) {
            super(id);
        }
    }

    /**
     * @return content file.
     */
    public File getContentFile() {
        return fileIModel.getObject();
    }

    /**
     * @return available MIME types.
     */
    protected abstract List<String> getAvailibleMimeTypes();

    /**
     * @return additionnal panel.
     */
    protected abstract WebMarkupContainer getAdditionalPanel();

}
