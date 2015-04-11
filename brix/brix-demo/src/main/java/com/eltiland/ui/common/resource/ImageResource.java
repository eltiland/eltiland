package com.eltiland.ui.common.resource;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.resource.WebExternalResourceStream;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author knorr
 * @version 1.0
 * @since 7/27/12
 */
public class ImageResource extends AbstractResource {

    private static final String CACHE_PREFIX = "context-relative:/";

    public static final String IMAGE_ID_URL_PARAMETER = "id";

    public static final String IMAGE_PREVIEW_INDICATOR = "preview";

    @SpringBean
    private FileManager fileManager;

    @SpringBean
    private FileUtility fileUtility;

    public ImageResource() {
        Injector.get().inject(this);

    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {

        //Load file from DB
        PageParameters parameters = attributes.getParameters();
        if (parameters.get(IMAGE_ID_URL_PARAMETER) == null) {
            throw new WicketRuntimeException("File not found. Exiting.");
        }

        Long imageId = (Long) parameters.get(IMAGE_ID_URL_PARAMETER).to(Long.class);
        File imageFile = fileManager.getFileById(imageId);
        //TODO: check file is image

        //Create response
        final ResourceResponse response = new ResourceResponse();

        //If no-file was returned from DB, then return 200 status.
        if (imageFile == null) {
            response.setError(HttpServletResponse.SC_NOT_FOUND);
            return response;
        }

        //Fill-in response data.
        response.setCacheDuration(WebResponse.MAX_CACHE_DURATION);
        if (response.dataNeedsToBeWritten(attributes)) {
            response.setContentType(imageFile.getType());
            response.setContentDisposition(ContentDisposition.INLINE);
            final byte[] imageData = getImageData(imageFile, attributes);
            if (imageData == null) {
                response.setError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(final Attributes attributes) {
                        attributes.getResponse().write(imageData);
                    }
                });
                configureResponse(response, attributes);
            }
        }
        return response;
    }

    /**
     * Override to modify response properties.
     *
     * @param response   http response
     * @param attributes request attributes
     */
    protected void configureResponse(final ResourceResponse response, final Attributes attributes) {
    }

    /**
     * Load image data!
     *
     * @param file eltiland file
     * @return file bytes
     */
    protected byte[] getImageData(File file, Attributes attributes) {
        if (attributes.getParameters().get(IMAGE_PREVIEW_INDICATOR).toBoolean()) {
            //Image preview stored  in DB! (or FS?)
            if (file.getPreviewBody().getBody() != null) {
                return file.getPreviewBody().getBody();
            } else {
                WebExternalResourceStream externalResource = new WebExternalResourceStream(file.getPreviewBody().getFilename());
                InputStream inputStream = null;
                try {
                    inputStream = externalResource.getInputStream();
                    return IOUtils.toByteArray(inputStream);
                } catch (ResourceStreamNotFoundException | IOException e) {
                    throw new WicketRuntimeException(e);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }
        //File body stored on FS
        try {
            IResourceStream resourceStream = fileUtility.getFileResource(file.getBody().getHash());
            InputStream stream = resourceStream.getInputStream();
            return IOUtils.toByteArray(stream);
        } catch (ResourceStreamNotFoundException | IOException e) {
            throw new WicketRuntimeException(e);
        }
    }

    // Needed by ResourceMapper to be able to match the request Url with
    // the mounted ResourceReference
    @Override
    public boolean equals(Object that) {
        return that instanceof ImageResource;
    }

}