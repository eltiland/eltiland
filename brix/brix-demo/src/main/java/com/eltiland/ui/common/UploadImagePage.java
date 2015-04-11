package com.eltiland.ui.common;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.FileManager;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUploadCallback;
import com.eltiland.ui.common.components.upload.ELTUploadPanel;
import com.eltiland.ui.common.resource.ImageResource;
import com.eltiland.ui.common.resource.ImageResourceReference;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * A special page used by TinyMCE
 */
public class UploadImagePage extends WebPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadImagePage.class);

    @SpringBean
    private FileManager fileManager;

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;


    public UploadImagePage() {
        ELTUploadPanel uploadPanel = new ELTUploadPanel("uploadPanel", Arrays.asList(MimeTypes.IMAGE));
        uploadPanel.setUploadCallback(new IDialogUploadCallback.IDialogActionProcessor<FileUpload>() {
            @Override
            public void process(IModel<FileUpload> uploadedFileModel, AjaxRequestTarget target) {
                try {
                    File f = fileManager.saveFile(fileManager.createFileFromUpload(uploadedFileModel.getObject()));
                    PageParameters pp = new PageParameters();
                    pp.add(ImageResource.IMAGE_ID_URL_PARAMETER, f.getId());

                    String url = eltilandProps.getProperty("application.base.url")
                            + urlFor(new ImageResourceReference(), pp).toString().substring(1);

                    target.appendJavaScript("javascript:FileBrowserDialogue.mySubmit('" + url + "');");
                } catch (FileException e) {
                    LOGGER.error("Error saving file", e);
                    ELTAlerts.renderErrorPopup("Ошибка загрузки файла.", target);
                }
            }
        });

        add(uploadPanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(ResourcesUtils.CSS_ELT_STYLE);
        response.renderCSSReference(ResourcesUtils.CSS_JQUERY);
        response.renderCSSReference(ResourcesUtils.CSS_COMPONENTS);

        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_UI);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_COMPONENTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_FUNCTION);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);

        response.renderJavaScriptReference(TinyMceEnabler.MCE_POPUP_JS);

        response.renderOnDomReadyJavaScript(String.format("tryRegisterWicketAjaxOnFailure('%s')",
                getString("unreachableServerMessage").replaceAll("\\n", "")));

        EltiStaticAlerts.renderOKPopups(response);

    }
}
