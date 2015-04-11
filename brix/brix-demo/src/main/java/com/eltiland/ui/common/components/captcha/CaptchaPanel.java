package com.eltiland.ui.common.components.captcha;

import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Autonomous anti spam panel.
 *
 * @author Vadim Didenko
 */
public class CaptchaPanel extends FormComponentPanel<Boolean> {

    private RussianCaptchaImageResource captchaImageResource = new RussianCaptchaImageResource();

    /**
     * Captcha image.
     */
    private Image captchaImage = new Image("captchaImage", captchaImageResource) {
        @Override
        protected boolean shouldAddAntiCacheParameter() {
            return true;
        }
    };

    private EltiAjaxLink<String> resetCaptcha = new EltiAjaxLink<String>("resetCaptchaLink") {

        @Override
        public void onClick(AjaxRequestTarget target) {
            captchaImageResource = new RussianCaptchaImageResource();
            captchaImage.setImageResource(captchaImageResource);
            target.add(captchaImage);
        }
    };

    /**
     * Captcha field.
     */
    private ELTTextField<String> captchaField = new ELTTextField<String>(
            "captchaField", new ResourceModel("captchaLabel"), new Model<String>(), String.class, true) {
        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            // clear the field after each render
            tag.put("value", "");
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            add(new AbstractValidator<String>() {
                @Override
                protected void onValidate(IValidatable<String> validatable) {
                    if (!captchaImageResource.getChallengeId().equals(validatable.getValue())) {
                        ValidationError ve = new ValidationError();
                        ve.addMessageKey("captchaError");
                        captchaField.error(ve);
                        captchaImageResource.invalidate();
                    }
                }
            });
        }
    };

    public CaptchaPanel(String id) {
        super(id, new Model<Boolean>());
        add(captchaImage.setOutputMarkupId(true));
        add(captchaField.setOutputMarkupId(true));
        add(resetCaptcha);

        WebMarkupContainer refreshIcon = new WebMarkupContainer("refreshIcon");
        resetCaptcha.add(refreshIcon);

        refreshIcon.add(new AttributeModifier("title", new ResourceModel("reset")));
        refreshIcon.add(new TooltipBehavior());
    }

    @Override
    protected void convertInput() {
        setConvertedInput(captchaField.getConvertedInput().equals(captchaImageResource.getChallengeId()));
    }


}
