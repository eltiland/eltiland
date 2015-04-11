package com.eltiland.ui.common.column.image;

import com.eltiland.bl.FileManager;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.resource.StaticImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author knorr
 * @version 1.0
 * @since 8/13/12
 */
public class ImagePanel extends Panel {

    @SpringBean
    private FileManager fileManager;

    public ImagePanel(String id, File imageFile, boolean isPreview) {
        super(id);
        add(new StaticImage("image", imageFile.getId(), isPreview)); //TODO: doublecheck
    }

    public ImagePanel(String id, String url) {
        super(id);
        add(new StaticImage("image", url));
    }
}
