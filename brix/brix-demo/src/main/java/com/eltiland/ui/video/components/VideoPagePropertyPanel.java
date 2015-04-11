package com.eltiland.ui.video.components;

import com.eltiland.bl.PropertyManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Property;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for setting count of videos on page.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VideoPagePropertyPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(VideoPagePropertyPanel.class);

    @SpringBean
    private PropertyManager propertyManager;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public VideoPagePropertyPanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);

        Integer pageValue = Integer.parseInt(propertyManager.getProperty(Property.VIDEO_PAGING));

        final ELTTextField<Integer> pageCountField = new ELTTextField<>(
                "videoCountField", new ResourceModel("pageCountHeader"), new Model<Integer>(), Integer.class, true);

        pageCountField.setModelObject(pageValue);

        pageCountField.add(new AbstractValidator() {
            @Override
            protected void onValidate(IValidatable validatable) {
                Integer value = (Integer) validatable.getValue();
                if (value.intValue() < 1) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("minValueError");
                        }
                    });
                }
            }
        });

        form.add(pageCountField);
        form.add(new FormRequired("required"));

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    propertyManager.saveProperty(Property.VIDEO_PAGING, pageCountField.getModelObject().toString());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update property value", e);
                    throw new WicketRuntimeException(e);
                }
                EltiStaticAlerts.registerOKPopup(getString("propertySaveMessage"));
                onSave(target);
            }
        });
    }



    protected abstract void onSave(AjaxRequestTarget target);
}
