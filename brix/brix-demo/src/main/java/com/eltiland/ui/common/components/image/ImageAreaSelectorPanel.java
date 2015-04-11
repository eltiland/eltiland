package com.eltiland.ui.common.components.image;

import com.eltiland.bl.FileManager;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.utils.ImageUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author knorr
 * @version 1.0
 * @since 7/27/12
 */
public abstract class ImageAreaSelectorPanel extends BaseEltilandPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAreaSelectorPanel.class);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    @SpringBean
    private FileManager fileManager;

    private transient BufferedImage bufferedImage;

    /*
    * Avatar panel components.
    */
    private HiddenField<Long> x = new HiddenField<Long>("x", new Model<Long>(), Long.class);
    private HiddenField<Long> y = new HiddenField<Long>("y", new Model<Long>(), Long.class);
    private HiddenField<Long> width = new HiddenField<Long>("width", new Model<Long>(), Long.class);
    private HiddenField<Long> height = new HiddenField<Long>("height", new Model<Long>(), Long.class);

    /**
     * Image for preview area selecting.
     */
    private Image image;


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        //load "Image Area Select" javascript plugin
        response.renderCSSReference(ResourcesUtils.CSS_IMAGE_AREA_SELECTOR);
        response.renderJavaScriptReference(ResourcesUtils.JS_IMAGE_AREA_SELECTOR);
    }

    @Override
    protected void onModelChanged() {
        FileUpload upload = (FileUpload) ImageAreaSelectorPanel.this.getDefaultModelObject();
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(upload.getBytes()));
            image.setImageResource(new ByteArrayResource(upload.getContentType(), upload.getBytes()));
        } catch (IOException e) {
            LOGGER.error("Get an exception when trying to read uploaded image!", e);
        }
    }

    /**
     * Get default selection size.
     *
     * @return
     */
    private int getSelectionSize() {
        return Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()) / 3;
    }

    public ImageAreaSelectorPanel(String id, IModel<FileUpload> uploadModel) {
        super(id, uploadModel);
        FileUpload fileUpload = uploadModel.getObject();
        ByteBuffer byteBuffer = ByteBuffer.wrap(fileUpload.getBytes());
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(byteBuffer.array()));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        Behavior areaSelectorBehavior = new Behavior() {

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                String mId = component.getMarkupId();
                response.renderOnLoadJavaScript(String.format(
                        " var formParams = {};"
                                + " formParams['x'] = '%s';"
                                + " formParams['y'] = '%s';"
                                + " formParams['width'] = '%s';"
                                + " formParams['height'] = '%s';"
                                + " var props = {};"
                                + " props['aspectRatio'] = '%s';"
                                + " props['x2'] = %d;"
                                + " props['y2'] = %d;"
                                + " props['imageHeight'] = %d;"
                                + " props['imageWidth'] = %d;"
                                + " ImageResizeTool('%s', props, formParams);",
                        x.getInputName(),
                        y.getInputName(),
                        width.getInputName(),
                        height.getInputName(),
                        getAspectRatio(),
                        getSelectionSize(),
                        getSelectionSize(),
                        bufferedImage.getHeight(),
                        bufferedImage.getWidth(),
                        mId));
            }
        };
        setOutputMarkupId(true);
        Form resizeParamsForm = new Form("imageAreaParamForm");
        resizeParamsForm.setMultiPart(true);
        add(resizeParamsForm);
        resizeParamsForm.add(x);
        resizeParamsForm.add(y);
        resizeParamsForm.add(width);
        resizeParamsForm.add(height);
        image = new Image("image", new ByteArrayResource(fileUpload.getContentType(), byteBuffer.array()));
        image.setOutputMarkupId(true);
        image.add(areaSelectorBehavior);
        resizeParamsForm.add(image);
        resizeParamsForm.add(new EltiAjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                //Workaround for small images(Detected bug in JS plugin).
                int xLen = x.getModelObject().intValue();
                int yLen = y.getModelObject().intValue();
                int wLen = width.getModelObject().intValue();
                int hLen = height.getModelObject().intValue();
                if (xLen < 0 || xLen + wLen > bufferedImage.getWidth()) {
                    xLen = 0;
                }
                if (yLen < 0 || yLen + hLen > bufferedImage.getHeight()) {
                    yLen = 0;
                }
                //Cut selected area.
                BufferedImage avatar = bufferedImage.getSubimage(xLen, yLen, wLen, hLen);
                //Scale selected area to standard size
                int type = avatar.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : avatar.getType();
                avatar = ImageUtils.resizeImage(avatar, type,
                        Integer.valueOf(getWidth()),
                        Integer.valueOf(getHeight()));
                //Create and persis image file.


                //Create and persist image file.
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    if (getQuality() != 1) {
                        File file = new File("temp.jpg");
                        FileImageOutputStream imageOutputStream = new FileImageOutputStream(file);

                        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
                        ImageWriter writer = (ImageWriter) iter.next();
                        ImageWriteParam iwp = writer.getDefaultWriteParam();
                        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        float quality = getQuality();
                        iwp.setCompressionQuality(quality);
                        writer.setOutput(imageOutputStream);
                        IIOImage image = new IIOImage(avatar, null, null);
                        writer.write(null, image, iwp);
                        writer.dispose();

                        byte[] bytes = Files.readAllBytes(Paths.get("temp.jpg"));
                        stream = new ByteArrayOutputStream(bytes.length);
                        stream.write(bytes, 0, bytes.length);
                        file.delete();
                    } else {
                        ImageIO.write(avatar, "png", stream);
                    }

                    FileUpload upload = (FileUpload) ImageAreaSelectorPanel.this.getDefaultModelObject();
                    doProcessCreateImagePreview(upload, stream, target);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        });
    }

    public abstract void doProcessCreateImagePreview(
            FileUpload uploadedFile,
            ByteArrayOutputStream imagePreviewOutputStream,
            AjaxRequestTarget target);

    public abstract String getAspectRatio();

    public abstract String getWidth();

    public abstract String getHeight();

    public abstract float getQuality();
}