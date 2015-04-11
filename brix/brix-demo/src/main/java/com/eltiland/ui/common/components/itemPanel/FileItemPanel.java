package com.eltiland.ui.common.components.itemPanel;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Panel for display file item.
 *
 * @author Igor Cherednichenko
 * @see AbstractItemPanel
 */
public class FileItemPanel extends AbstractItemPanel<File> {

    @SpringBean
    private GenericManager genericManager;

    @SpringBean
    private FileManager fileManager;

    @SpringBean
    private FileUtility fileUtility;

    /**
     * Default constructor.
     *
     * @param id   wicket id
     * @param file file instance
     */
    public FileItemPanel(String id, File file) {
        super(id);

        //lazy model initialisation
        setModel(new FileModel(file));
    }

    @Override
    protected String getItemType() {
        if (getModelObject() == null) {
            return null;
        }
        return getString("fileTypeName");
    }

    @Override
    protected String getItemName() {
        if (getModelObject() == null) {
            return null;
        }
        return getModelObject().getName();
    }

    @Override
    protected boolean isDownloadingAllowed() {
        return getModelObject() != null;
    }

    @Override
    protected String getDownloadFileName() {
        return getModelObject().getName();
    }

    @Override
    protected IResourceStream getDownloadFileResourceStream() {
        if (getModelObject().getId() == null) {
            //if is not persisted transient file
            return fileUtility.getFileResource(getModelObject().getBody().getHash());
        }
        return fileUtility.getFileResource(fileManager.getFileById(getModelObject().getId()).getBody().getHash());
    }

    @Override
    protected File getPreviewImageFile() {
        File file = getModelObject();

        //TODO
        if (file != null) {
            genericManager.initialize(file, file.getPreviewBody());
        }
        return file;
    }

    /**
     * Internal implementation of file model.
     * <p/>
     * This model not implement detach process to keep transient file instance as long as possible.
     */
    private class FileModel extends LoadableDetachableModel<File> {
        @SpringBean
        GenericManager manager;

        private transient File transientFile;
        private Long persistentFileId;

        private FileModel(File object) {
            Injector.get().inject(this);
            setObject(object);
        }

        @Override
        protected File load() {
            if (persistentFileId != null) {
                return manager.getObject(File.class, persistentFileId);
            }
            return transientFile;
        }

        @Override
        protected void onDetach() {
            if (getObject() == null || getObject().getId() == null) {
                transientFile = getObject();
                persistentFileId = null;
            } else {
                transientFile = null;
                persistentFileId = getObject().getId();
            }
        }
    }
}
