package com.eltiland.ui.common.resource;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * @author knorr
 * @version 1.0
 * @since 7/27/12
 */
public class ImageResourceReference extends ResourceReference {

    public ImageResourceReference() {
        super(ImageResourceReference.class, "images");
    }

    @Override
    public IResource getResource() {
        return new ImageResource();
    }

}