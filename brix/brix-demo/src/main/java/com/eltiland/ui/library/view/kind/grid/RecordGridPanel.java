package com.eltiland.ui.library.view.kind.grid;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.model.library.*;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.library.panels.view.RecordViewPage;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Record panel for grid view.
 *
 * @author Aleksey Plotnikov.
 */
public class RecordGridPanel extends BaseEltilandPanel<LibraryRecord> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RecordGridPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;
    @SpringBean
    private FileManager fileManager;

    public static final String CSS = "static/css/library/record.css";

    /**
     * Panel ctor.
     *
     * @param id                  markup id.
     * @param libraryRecordIModel library record model.
     */
    public RecordGridPanel(String id, IModel<LibraryRecord> libraryRecordIModel) {
        super(id, libraryRecordIModel);

        final Class<? extends LibraryRecord> clazz = getModelObject().getClass();
        if (clazz.equals(LibraryImageRecord.class)) {
            final IModel<File> fileIModel = new LoadableDetachableModel<File>() {
                @Override
                protected File load() {
                    genericManager.initialize(getModelObject(), getModelObject().getFileContent());
                    return fileManager.getFileById(getModelObject().getFileContent().getId());
                }
            };

            IResourceStream resourceStream = new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    return new ByteArrayInputStream(fileIModel.getObject().getPreviewBody().getBody());
                }

                @Override
                public void close() throws IOException {
                }
            };
            try {
                IResource resource = new ByteArrayResource(fileIModel.getObject().getType(),
                        IOUtils.toByteArray(resourceStream.getInputStream()), fileIModel.getObject().getName());
                add(new Image("icon", resource));
            } catch (ResourceStreamNotFoundException | IOException e) {
                LOGGER.error("Cannot show record", e);
                throw new WicketRuntimeException(e);
            }
        } else {
            String url = null;
            if (clazz.equals(LibraryDocumentRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_DOCUMENT.getPath();
            } else if (clazz.equals(LibraryPresentationRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_PRESENTATION.getPath();
            } else if (clazz.equals(LibraryArchiveRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_ARCHIVE.getPath();
            } else if (clazz.equals(LibraryVideoRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_VIDEO.getPath();
            }

            StaticImage icon = new StaticImage("icon", url);
            add(icon);
        }

        add(new Label("label", getString(getModelObject().getClass().getSimpleName() + ".type")));

        add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                boolean canBeViewed;
                if (clazz.equals(LibraryDocumentRecord.class) || clazz.equals(LibraryPresentationRecord.class)) {
                    canBeViewed = RecordGridPanel.this.getModelObject().isPublished();
                } else {
                    canBeViewed = clazz.equals(LibraryImageRecord.class) || clazz.equals(LibraryVideoRecord.class);
                }
                if (canBeViewed) {
                    throw new RestartResponseException(RecordViewPage.class,
                            new PageParameters().add(RecordViewPage.PARAM_ID,
                                    RecordGridPanel.this.getModelObject().getId()));
                } else {
                    ELTAlerts.renderErrorPopup(getString("previewError"), target);
                }
            }
        });

        add(new AttributeModifier("title", new Model<>(getModelObject().getName())));
        add(new TooltipBehavior());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
