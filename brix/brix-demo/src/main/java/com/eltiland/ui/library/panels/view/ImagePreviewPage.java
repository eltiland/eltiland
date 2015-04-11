package com.eltiland.ui.library.panels.view;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.button.back.BackButton;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.image.Image;
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
import java.io.InputStream;

/**
 * Page for preview library image.
 *
 * @author Aleksey Plotnikov.
 */
public class ImagePreviewPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePreviewPage.class);

    public static final String MOUNT_PATH = "/imagePreview";
    public static final String PARAM_ID = "id";

    /**
     * Page ctor.
     *
     * @param parameters page parameters.
     */
    public ImagePreviewPage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        File image = genericManager.getObject(File.class, parameters.get(PARAM_ID).toLong());
        if (image == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new BackButton("backButton"));

        genericManager.initialize(image, image.getBody());
        IResourceStream resourceStream = fileUtility.getFileResource(image.getBody().getHash());
        InputStream stream = null;
        try {
            stream = resourceStream.getInputStream();
        } catch (ResourceStreamNotFoundException e) {
            LOGGER.error("Cannot show image record", e);
            throw new WicketRuntimeException(e);
        }
        IResource resource = null;
        try {
            resource = new ByteArrayResource(image.getType(), IOUtils.toByteArray(stream), image.getName());
        } catch (IOException e) {
            LOGGER.error("Cannot show image record", e);
            throw new WicketRuntimeException(e);
        }

        add(new Image("imagePanel", resource));
    }
}
