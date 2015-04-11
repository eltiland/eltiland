package com.eltiland.ui.common.components.file;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.File;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inner smart LoadableDetachableModel with support mixed entity list (existed and new)
 */
class FileWrapperModel extends LoadableDetachableModel<List<FileWrapper>> {
    @SpringBean
    private GenericManager manager;
    @SpringBean
    private FileManager fileManager;


    private List<Long> existedFileIds = new ArrayList<>();

    private boolean hasNewFiles;
    private transient Set<FileWrapper> newFiles = new HashSet<>();

    public FileWrapperModel() {
        Injector.get().inject(this);
    }

    public FileWrapperModel(List<FileWrapper> object) {
        super(object);
        Injector.get().inject(this);
    }

    @Override
    protected List<FileWrapper> load() {
        List<FileWrapper> result = new ArrayList<>();

        if (newFiles == null) {
            newFiles = new HashSet<>();
        }

        if (existedFileIds.isEmpty() && newFiles.isEmpty()) {
            return result;
        }

        if (!existedFileIds.isEmpty()) {
            List<File> existedFiles = fileManager.getFileListByIds(existedFileIds);
            for (File file : existedFiles) {
                result.add(new FileWrapper(file.getName(), file));
            }
        }

        result.addAll(newFiles);

        return result;
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        existedFileIds.clear();
        newFiles.clear();
        hasNewFiles = false;

        for (FileWrapper fileWrapper : getObject()) {
            if (fileWrapper.file.getId() != null) {
                existedFileIds.add(fileWrapper.file.getId());
            } else {
                newFiles.add(fileWrapper);
                hasNewFiles = true;
            }
        }
    }

    public boolean isNewFilesLost() {
        if (!isAttached()) {
            getObject();
        }
        return hasNewFiles && newFiles.isEmpty();
    }
}