package com.eltiland.bl.impl.previews;

import com.eltiland.bl.PreviewProcessor;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;
import com.eltiland.utils.ImageUtils;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author knorr
 * @version 1.0
 * @since 7/26/12
 */
@Component
public class ImagePreviewProcessor implements PreviewProcessor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePreviewProcessor.class);

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    @Qualifier("default")
    private PreviewProcessor defaultPreviewProcessor;

    /**
     * {@inheritDoc}
     */
    @Override
    public File createPreview(FileUpload file)
    {
        File icon = new File();
        icon.setSize(file.getSize());
        icon.setName(file.getClientFileName());
        icon.setType(file.getContentType());

        FileBody body = new FileBody();
        body.setHash(fileUtility.saveTemporalFile(file.getBytes()));

        FileBody preview = new FileBody();
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        final int previewImageHeight = Integer.valueOf(eltilandProps.getProperty("avatar.height"));
        final int previewImageWidth = Integer.valueOf(eltilandProps.getProperty("avatar.width"));

        try
        {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (original == null)
            {
                LOGGER.warn("cannot resize image of type: " + file.getContentType() + " Use default icon");
                return defaultPreviewProcessor.createPreview(file);
            }
            int type = original.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : original.getType();
            int width = original.getWidth();
            int height = original.getHeight();
            double ratio = (double) width / height;
            if (height > width)
            {
                width = (int) (ratio * previewImageHeight);
                height = previewImageHeight;
            } else
            {
                height = (int) (1 / ratio * previewImageWidth);
                width = previewImageWidth;
            }
            BufferedImage result = ImageUtils.resizeImage(original, type, width, height);
            ImageIO.write(result, "png", imageOutputStream);
        } catch (IOException e)
        {
            LOGGER.error("Can't read image, get an I/O exception", e);
        }
        preview.setBody(imageOutputStream.toByteArray());

        icon.setBody(body);
        icon.setPreviewBody(preview);
        return icon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompatible(String mimeType)
    {
        return MimeTypes.getTypeOf(mimeType).equals(MimeTypes.IMAGE);
    }

}
