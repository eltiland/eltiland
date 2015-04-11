package com.eltiland.ui.worktop.simple.panel.files.panels;

import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for file upload.
 */
public class UploadFilePanel extends ELTDialogPanel implements IDialogNewCallback<File> {

    private IDialogActionProcessor<File> callback;

    private ELTFilePanel filePanel = new ELTFilePanel("filePanel") {
        @Override
        protected int getMaxFiles() {
            return 1;
        }
    };

    public UploadFilePanel(String id) {
        super(id);
        form.add(filePanel);
        form.setMultiPart(true);
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
            try {
                file = filePanel.getFiles(true).get(0);
            } catch (FileException e) {
                ELTAlerts.renderErrorPopup(e.getMessage(), target);
            }
            if (file != null) {
                callback.process(new GenericDBModel<>(File.class, file), target);
            }
        }
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<File> callback) {
        this.callback = callback;
    }
}
