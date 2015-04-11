package com.eltiland.ui.library.panels.view;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.library.*;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.common.components.video.YoutubeVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.library.LibraryPage;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Page for view record content.
 *
 * @author Aleksey Plotnikov.
 */
public class RecordViewPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordViewPage.class);

    private IModel<LibraryRecord> recordIModel = new GenericDBModel<>(LibraryRecord.class);

    public static final String MOUNT_PATH = "/record";
    public static final String PARAM_ID = "id";

    /**
     * Page constructor.
     *
     * @param parameters page params.
     */
    public RecordViewPage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final LibraryRecord record = genericManager.getObject(LibraryRecord.class, parameters.get(PARAM_ID).toLong());
        final Class<? extends LibraryRecord> clazz = record.getClass();
        if (record == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        recordIModel.setObject(record);

        add(new Label("name", record.getName()));

        add(new BackButton("backButton") {
            @Override
            protected void onAction(AjaxRequestTarget target) {
                if (clazz.equals(LibraryPresentationRecord.class)) {
                    throw new RestartResponseException(LibraryPage.class);
                } else {
                    super.onAction(target);
                }
            }
        });

        WebMarkupContainer documentContainer = new WebMarkupContainer("documentContainer") {
            @Override
            public boolean isVisible() {
                return (recordIModel.getObject() instanceof LibraryDocumentRecord ||
                        recordIModel.getObject() instanceof LibraryPresentationRecord);
            }
        };

        WebMarkupContainer imageContainer = new WebMarkupContainer("imageContainer") {
            @Override
            public boolean isVisible() {
                return recordIModel.getObject() instanceof LibraryImageRecord;
            }
        };

        WebMarkupContainer videoContainer = new WebMarkupContainer("videoContainer") {
            @Override
            public boolean isVisible() {
                return recordIModel.getObject() instanceof LibraryVideoRecord;
            }
        };

        add(documentContainer);
        add(imageContainer);
        add(videoContainer);

        if (record instanceof LibraryDocumentRecord || record instanceof LibraryPresentationRecord) {
            genericManager.initialize(record, record.getContent());
            GoogleDriveFile.TYPE type = (record instanceof LibraryDocumentRecord) ?
                    GoogleDriveFile.TYPE.DOCUMENT : GoogleDriveFile.TYPE.PRESENTATION;
            documentContainer.add(new ELTGoogleDriveEditor("content",
                    new GenericDBModel<>(GoogleDriveFile.class, record.getContent()),
                    ELTGoogleDriveEditor.MODE.VIEW, type));
        } else if (record instanceof LibraryImageRecord) {
            genericManager.initialize(record, record.getFileContent());
            genericManager.initialize(record.getFileContent(), record.getFileContent().getBody());
            IResourceStream resourceStream = fileUtility.getFileResource(record.getFileContent().getBody().getHash());
            try {
                IResource resource = new ByteArrayResource(record.getFileContent().getType(),
                        IOUtils.toByteArray(resourceStream.getInputStream()),
                        record.getFileContent().getName());
                imageContainer.add(new Image("image", resource));
            } catch (ResourceStreamNotFoundException | IOException e) {
                LOGGER.error("Cannot show record", e);
                throw new WicketRuntimeException(e);
            }
        } else if (record instanceof LibraryVideoRecord) {
            videoContainer.add(new YoutubeVideoPlayer("player",
                    new Model<>(((LibraryVideoRecord) record).getVideoLink())));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LIBRARY);
    }
}
