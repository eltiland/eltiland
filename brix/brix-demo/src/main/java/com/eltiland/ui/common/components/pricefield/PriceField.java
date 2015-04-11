package com.eltiland.ui.common.components.pricefield;

import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Control for price input/output.
 *
 * @author Aleksey Plotnikov
 */
public class PriceField extends FormComponentPanel<BigDecimal> {

    private PriceTextField amountField = new PriceTextField("amount", new Model<BigDecimal>());
    private WebMarkupContainer requiredAsterisk = new WebMarkupContainer("asteriskRequired");

    public PriceField(String id, IModel<String> headerModel) {
        super(id);
        addComponents(headerModel);
    }

    public PriceField(String id, IModel<String> headerModel, IModel<BigDecimal> model) {
        super(id, model);
        addComponents(headerModel);
    }

    public void setValue(BigDecimal value) {
        amountField.setModelObject(value);
        convertInput();
    }

    public void setEnabledValue(boolean isEnabled) {
        amountField.setEnabled(isEnabled);
    }

    public BigDecimal getPriceValue() {
        Object value = amountField.getValue();
        String strValue = value.toString();

        return new BigDecimal(convertStringValue(strValue));
    }

    public void addBehavior(Behavior behavior) {
        amountField.add(behavior);
    }

    @Override
    protected void onBeforeRender() {
        requiredAsterisk.setVisible(isRequired());
        amountField.setRequired(isRequired());
        super.onBeforeRender();
    }

    @Override
    protected void convertInput() {
        Object value = amountField.getValue();
        if (value != null && !(value.toString().isEmpty())) {
            String strValue = value.toString();
            setConvertedInput(new BigDecimal(convertStringValue(strValue)));
        } else {
            setConvertedInput(BigDecimal.ZERO);
        }
    }

    private void addComponents(IModel<String> headerModel) {
        add(new ELTFeedbackLabel("label", headerModel, amountField));
        add(amountField.setOutputMarkupId(true));
        add(requiredAsterisk);
    }

    private class PriceTextField extends TextField<BigDecimal> {

        private static final String FORMATTED_FLOAT_PATTERN = "^(-)?\\d{1,3}(.\\d{3})*((,|.)\\d+)?";

        public PriceTextField(String id, IModel<BigDecimal> model) {
            super(id, model);
            setOutputMarkupId(true);
            add(new Behavior() {
                @Override
                public void renderHead(Component component, IHeaderResponse response) {
                    response.renderOnDomReadyJavaScript(String.format("FloatFormatter('%s')", getMarkupId()));
                }
            });

            add(new PatternValidator(FORMATTED_FLOAT_PATTERN) {
                @Override
                public void error(IValidatable<String> validatable) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("errorMoneyFormat");
                        }
                    });
                }
            });

            add(new AbstractValidator<BigDecimal>() {
                @Override
                protected void onValidate(IValidatable<BigDecimal> validatable) {
                    Object value = validatable.getValue();
                    String strValue = value.toString();

                    if (strValue.length() > 8) {
                        validatable.error(new IValidationError() {
                            @Override
                            public String getErrorMessage(IErrorMessageSource messageSource) {
                                return getString("errorTooMuchMoney");
                            }
                        });
                    } else {
                        BigDecimal price = new BigDecimal(convertStringValue(strValue));

                        if (price.compareTo(BigDecimal.ZERO) == -1) {
                            validatable.error(new IValidationError() {
                                @Override
                                public String getErrorMessage(IErrorMessageSource messageSource) {
                                    return getString("errorNegativeMoney");
                                }
                            });
                        } else if (price.compareTo(BigDecimal.ZERO) == 0) {
                            validatable.error(new IValidationError() {
                                @Override
                                public String getErrorMessage(IErrorMessageSource messageSource) {
                                    return getString("errorZeroMoney");
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private String convertStringValue(String value) {
        value = StringUtils.remove(value, (char) 160);
        int decIndex = value.indexOf(",");
        if ((decIndex + 2) >= (value.length() - 1)) {
            value = value.replace(",", ".");
        } else {
            value = value.replace(",", "");
        }
        return value;
    }
}
