package com.eltiland.ui.service.plugin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.IconsLoader;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
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
    private GoogleDriveManager googleDriveManager;

    protected ServicePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        Injector.get().inject(this);

        LOGGER.info("Entered to service panel");

        add(new EltiAjaxLink("cacheGoogleFiles") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<GoogleDriveFile> files = googleDriveManager.getFilesToCache();
                LOGGER.info(String.format("Founded %d files, starting to caching....", files.size()));
                ELTAlerts.renderWarningPopup(getString("cacheMessage"), target);

                int i = 0;
                for (GoogleDriveFile file : files) {
                    LOGGER.info(String.format("Caching file %d (%d/%d)", file.getId(), ++i, files.size()));
                    try {
                        googleDriveManager.cacheFile(file);
                    } catch (GoogleDriveException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    LOGGER.info(String.format("Caching file %d successfull", file.getId()));
                }
            }
        }.add(new ConfirmationDialogBehavior()));


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
    }
}
