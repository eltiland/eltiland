package com.eltiland.ui.google.buttons;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

import java.util.List;
import java.util.Properties;

/**
 * Abstract upload button.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractUploadButton extends BaseEltilandPanel {

    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private FileUtility fileUtility;
    @SpringBean
    private FileManager fileManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    protected FileUploadField uploadField = new FileUploadField("uploadField");

    /**
     * Max upload file size (default value)
     */
    private int maxFileUploadSize = Integer.valueOf(eltilandProps.getProperty("max.upload.file.size.mb"));

    public AbstractUploadButton(String id) {
        super(id);

        final Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                AbstractUploadButton.this.onSubmit();
            }
        };
        form.setOutputMarkupId(true);

        uploadField.add(new AjaxEventBehavior("onchange") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                target.appendJavaScript(String.format(new String("$(\"#%s\").trigger(\"submit\")"), form.getMarkupId()));
                target.appendJavaScript("bodyStartProgress();");
            }
        });

        form.add(uploadField);
        uploadField.add(new FileUploadSizeValidator());

        WebMarkupContainer buttonContainer = new WebMarkupContainer("button");
        buttonContainer.add(new AttributeModifier("class", getButtonClass()));
        WebMarkupContainer internalContainer = new WebMarkupContainer("internal");
        buttonContainer.add(internalContainer);
        internalContainer.add(new AttributeModifier("class", getInternalClass()));
        buttonContainer.add(getPanel());
        form.add(buttonContainer);
        add(form);
        uploadField.setOutputMarkupId(true);
        buttonContainer.setOutputMarkupId(true);

        Label script = new Label("script", new Model<String>());
        script.setEscapeModelStrings(false);
        script.setDefaultModelObject(String.format(new String("<script type=\"text/javascript\">\n" +
                "        $(\"#%s\").css('opacity', '0');\n" +
                "\n" +
                "        $(\"#%s\").click(function () {\n" +
                "            $(\"#%s\").trigger('click');\n" +
                "            return false;\n" +
                "        });\n" +
                "    </script>"), uploadField.getMarkupId(), buttonContainer.getMarkupId(), uploadField.getMarkupId()));
        add(script);
    }

    protected abstract void onSubmit();

    protected String getButtonClass() {
        return "google_button";
    }

    protected String getInternalClass() {
        return "img upload";
    }

    protected Panel getPanel() {
        return new EmptyPanel("panel");
    }

    private class FileUploadSizeValidator extends AbstractValidator {

        @Override
        public boolean validateOnNullValue() {
            return true;
        }

        @Override
        protected void onValidate(IValidatable iValidatable) {
            List<FileUpload> fileUploadList = (List<FileUpload>) iValidatable.getValue();
            if (fileUploadList == null) {
                return;
            }
            for (FileUpload fileUpload : fileUploadList) {
                if (fileUpload.getSize() > Bytes.megabytes(maxFileUploadSize).bytes()) {
                    ValidationError ve = new ValidationError();
                    ve.addMessageKey("uploadTooLargeForSingleFile");
                    ve.getVariables().put("fileName", fileUpload.getClientFileName());
                    ve.getVariables().put("maxSize", maxFileUploadSize);
                    uploadField.error(ve);
                    return;
                }
            }
        }
    }

    public abstract List<String> getAvailibleMimeTypes();
}
