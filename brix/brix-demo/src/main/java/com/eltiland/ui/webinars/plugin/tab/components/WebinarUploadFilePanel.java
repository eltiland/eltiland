package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.FileManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Panel for editing files, attached to webinar.
 *
 * @authoe Aleksey Plotnikov.
 */
public class WebinarUploadFilePanel extends ELTDialogPanel implements IDialogUpdateCallback<Webinar> {

    @SpringBean
    private FileManager fileManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private ELTUploadComponent uploadField = new ELTUploadComponent("uploadField");

    private IDialogActionProcessor<Webinar> updateCallback;

    public WebinarUploadFilePanel(String id) {
        super(id);

        form.setMultiPart(true);
        form.add(uploadField);
    }

    public void initData(IModel<Webinar> webinar) {
        this.webinarIModel = webinar;

        uploadField.setMaxCountUploadedFiles(3);
        uploadField.setUploadedFiles(fileManager.getFilesOfWebinar(webinarIModel.getObject()));
    }

    @Override
    protected String getHeader() {
        return getString("uploadHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            Webinar webinar = webinarIModel.getObject();
            webinar.setFiles(new HashSet<>(uploadField.getUploadedFiles()));
            updateCallback.process(new GenericDBModel<>(Webinar.class, webinar), target);
        }
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<Webinar> callback) {
        this.updateCallback = callback;
    }
}
