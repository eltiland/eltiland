package com.eltiland.ui.common.components.file;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Internal File item panel for file control.
 *
 * @author Aleksey Plotnikov.
 */
abstract class FilePanel extends BaseEltilandPanel<File> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    /**
     * Panel ctor.
     *
     * @param id         markup id.
     * @param fileIModel file object model.
     */
    public FilePanel(String id, IModel<File> fileIModel) {
        super(id, fileIModel);
        add(new Label("caption", fileIModel.getObject().getName()));

        WebMarkupContainer download = new WebMarkupContainer("download") {
            @Override
            public boolean isVisible() {
                return canBeDownloaded() && getModelObject().getId() != null;
            }
        };

        final AjaxDownload ajaxDownload = new AjaxDownload() {
            @Override
            protected IResourceStream getResourceStream() {
                genericManager.initialize(getModelObject(), getModelObject().getBody());
                return fileUtility.getFileResource(getModelObject().getBody().getHash());
            }

            @Override
            protected String getFileName() {
                return getModelObject().getName();
            }
        };

        download.add(ajaxDownload);
        download.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                ajaxDownload.initiate(target);
            }
        });

        WebMarkupContainer delete = new WebMarkupContainer("cancel") {
            @Override
            public boolean isVisible() {
                return canBeDeleted();
            }
        };


        delete.add(new ConfirmationDialogBehavior());
        delete.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onDelete(target);
            }
        });

        add(download);
        add(delete);

        download.add(new AttributeModifier("title", new ResourceModel("download")));
        download.add(new TooltipBehavior());
        delete.add(new AttributeModifier("title", new ResourceModel("delete")));
        delete.add(new TooltipBehavior());
    }

    /**
     * @return true if file element can be deleted.
     */
    protected abstract boolean canBeDeleted();

    /**
     * @return true if file element can be downloaded.
     */
    protected abstract boolean canBeDownloaded();

    /**
     * Event handler for delete action.
     */
    protected abstract void onDelete(AjaxRequestTarget target);
}
