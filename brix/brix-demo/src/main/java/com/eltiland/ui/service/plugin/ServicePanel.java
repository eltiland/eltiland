package com.eltiland.ui.service.plugin;

import com.eltiland.bl.CourseUserDataManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.IconsLoader;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.course.Course;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service function panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ServicePanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServicePanel.class);

    @SpringBean
    private IconsLoader iconsLoader;
    @SpringBean
    private IndexCreator indexCreator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseUserDataManager courseUserDataManager;
    @SpringBean
    private FileManager fileManager;

    protected ServicePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        Injector.get().inject(this);

        add(new EltiAjaxLink("reloadButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                iconsLoader.reloadIcons();
                ELTAlerts.renderOKPopup(getString("successMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("recreateSearchIndexesButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                indexCreator.doRebuildIndex();
                ELTAlerts.renderOKPopup(getString("recreateSearchIndexesMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("createStandartUserData") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Course course : genericManager.getEntityList(Course.class, "id")) {
                    try {
                        courseUserDataManager.createStandart(course);
                    } catch (EltilandManagerException e) {
                        e.printStackTrace();
                    }
                }

                ELTAlerts.renderOKPopup(getString("success"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        final ELTFilePanel filePanel = new ELTFilePanel("file", new GenericDBListModel<File>(File.class));
        add(filePanel);
        add(new EltiAjaxLink("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    List<File> files = filePanel.getFiles(true);
                } catch (FileException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

//        Form form = new Form("form");
//        add(form);
//
//        final FileUploadField uploadField = new FileUploadField("filePanel");
//        form.add(uploadField);
//        form.add(new EltiAjaxSubmitLink("submit") {
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                uploadField.getFileUpload().get
//            }
//        });
    }
}
