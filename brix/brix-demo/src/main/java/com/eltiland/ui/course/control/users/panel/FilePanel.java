package com.eltiland.ui.course.control.users.panel;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dialog panel for uploading file for listener.
 *
 * @author Aleksey Plotnikov.
 */
public class FilePanel extends ELTDialogPanel implements IDialogUpdateCallback<CourseListener> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePanel.class);

    private IDialogActionProcessor<CourseListener> callback;

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;

    private ELTUploadComponent documentField = new ELTUploadComponent("document", 1);

    private IModel<CourseListener> listenerIModel = new GenericDBModel<>(CourseListener.class);

    public FilePanel(String id) {
        super(id);
        form.add(documentField);
        form.setMultiPart(true);
    }

    public void initData(IModel<CourseListener> listenerIModel) {
        this.listenerIModel = listenerIModel;
        genericManager.initialize(this.listenerIModel.getObject(), this.listenerIModel.getObject().getAuthorDocument());
        if (this.listenerIModel.getObject().getAuthorDocument() != null) {
            documentField.setUploadedFiles(
                    new ArrayList<>(Arrays.asList(this.listenerIModel.getObject().getAuthorDocument())));
        }
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            File file = null;
            if (documentField.getUploadedFiles() != null) {
                file = documentField.getUploadedFiles().get(0);
                if (file != null) {
                    try {
                        fileManager.saveFile(file);
                    } catch (FileException e) {
                        LOGGER.error("Cannot save file", e);
                        throw new WicketRuntimeException("Cannot save file", e);
                    }
                }
            }

            listenerIModel.getObject().setAuthorDocument(file);
            callback.process(listenerIModel, target);
        }
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<CourseListener> callback) {
        this.callback = callback;
    }
}
