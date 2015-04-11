package com.eltiland.ui.common.panels.changeprice;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.components.pricefield.PriceField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.PatternValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Panel for changing price of paid service.
 *
 * @author Aleksey Plotnikov
 */
public class ChangePricePanel extends BaseEltilandPanel<BigDecimal> implements IDialogProcessCallback<BigDecimal> {

    private IDialogActionProcessor<BigDecimal> updateCallback;

    private Form form = new Form("form");
    private Label basePriceLabel = new Label("basePriceLabel");

    private TextField percentField = new TextField("percentField", new Model<String>());
    private ELTFeedbackLabel percentLabel = new ELTFeedbackLabel("percentLabel",
            new ResourceModel("pricePercentLabel"), percentField);

    private PriceField priceField = new PriceField("priceField",
            new ResourceModel("priceLabel"), new Model<BigDecimal>());

    private CheckBox freeModeCheckbox = new CheckBox("freeModeCheckbox", new Model<Boolean>());

    private static final String FORMATTED_FLOAT_PATTERN = "[-+]?[0-9]*\\.?[0-9]*";

    private EltiAjaxSubmitLink applyButton = new EltiAjaxSubmitLink("applyButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            if (updateCallback != null) {
                updateCallback.process(new Model<>(priceField.getPriceValue()), target);
            }
        }
    };

    private BigDecimal basePrice;

    private boolean isPercentEnabled = true;

    public ChangePricePanel(String id, final IModel<BigDecimal> childPaymentIModel) {
        super(id, childPaymentIModel);

        add(form);
        form.add(basePriceLabel);
        form.add(percentField.setOutputMarkupId(true));
        form.add(percentLabel);
        form.add(priceField.setRequired(true).setOutputMarkupId(true));
        form.add(applyButton);
        form.add(freeModeCheckbox);

        basePrice = childPaymentIModel.getObject();
        final BigDecimal hundredPercent = new BigDecimal(100);

        isPercentEnabled = ((basePrice != null && basePrice.floatValue() != 0));

        percentField.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String input = (String) ((TextField) this.getFormComponent()).getConvertedInput();
                BigDecimal outValue = new BigDecimal(0);

                if (input != null) {
                    BigDecimal dec = new BigDecimal(input);
                    outValue = basePrice.divide(hundredPercent).multiply(dec);
                }

                priceField.setValue(outValue);
                target.add(priceField);
            }

            @Override
            protected boolean getUpdateModel() {
                return false;
            }
        });

        percentField.add(new PatternValidator(FORMATTED_FLOAT_PATTERN) {
            @Override
            public void error(IValidatable<String> validatable) {
                validatable.error(new IValidationError() {
                    @Override
                    public String getErrorMessage(IErrorMessageSource messageSource) {
                        return getString("errorPercentFormat");
                    }
                });
            }
        });

        priceField.addBehavior(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String input = (String) ((TextField) this.getFormComponent()).getConvertedInput();
                String output = "";

                if (input != null) {
                    input = input.replace(",", ".");
                    BigDecimal inputValue = new BigDecimal(input);
                    inputValue = inputValue.setScale(2, RoundingMode.DOWN);
                    if (basePrice != null && (basePrice.floatValue() != 0)) {
                        output = inputValue.divide(basePrice, RoundingMode.DOWN).multiply(hundredPercent).toString();
                    } else {
                        output = BigDecimal.ZERO.toString();
                    }
                }
                percentField.setModelObject(output);
                target.add(percentField);
            }
        });

        freeModeCheckbox.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Boolean value = (Boolean) this.getFormComponent().getConvertedInput();
                if (value) {
                    percentField.setModelObject("0");
                    priceField.setValue(BigDecimal.ZERO);
                    percentField.setEnabled(false);
                    priceField.setEnabled(false);
                } else {
                    if (!percentField.isEnabled()) {
                        percentField.setEnabled(true);
                        priceField.setEnabled(true);
                    }
                }
                target.add(percentField);
                target.add(priceField);
            }
        });

        basePriceLabel.setDefaultModel(new Model<>(
                String.format(getString("basePriceLabel"), childPaymentIModel.getObject().toString())));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        priceField.setEnabled(true);
        percentField.setEnabled(isPercentEnabled);
        freeModeCheckbox.setModelObject(false);
        freeModeCheckbox.setVisible(willFreeCheckBoxShown());

        // priceField.setValue(null);
        // percentField.setModelObject(null);

    }

    public void initEditMode(BigDecimal value) {
        basePriceLabel.setDefaultModel(new Model<>(String.format(getString("basePriceLabel"), value.toString())));
        basePrice = value;
        priceField.setValue(value);
        if (value.intValue() > 0) {
            isPercentEnabled = true;
            percentField.setModelObject(BigDecimal.valueOf(100));
        }
    }

    @Override
    public void setProcessCallback(IDialogActionProcessor<BigDecimal> callback) {
        this.updateCallback = callback;
    }

    protected boolean willFreeCheckBoxShown() {
        return true;
    }
}
