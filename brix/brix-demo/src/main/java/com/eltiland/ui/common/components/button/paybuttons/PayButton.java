package com.eltiland.ui.common.components.button.paybuttons;

import com.eltiland.model.payment.PaidEntity;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

/**
 * Abstract Button with pay action to W1 processing center.
 *
 * @author Aleksey Plotnikov
 */
public abstract class PayButton extends BaseEltilandPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayButton.class);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    static final String PARAM_NAME = "name";
    static final String PARAM_VALUE = "value";

    static final String RUB_ID = "643";

    static final String W1_AMOUNT = "WMI_PAYMENT_AMOUNT";
    static final String W1_CURRENCY = "WMI_CURRENCY_ID";
    static final String W1_DESCRIPTION = "WMI_DESCRIPTION";
    static final String W1_MERCHANT = "WMI_MERCHANT_ID";
    static final String W1_SUCCESS_URL = "WMI_SUCCESS_URL";
    static final String W1_FAIL_URL = "WMI_FAIL_URL";
    static final String W1_PAYMENT_ID = "WMI_PAYMENT_NO";
    static final String W1_SIGNATURE = "WMI_SIGNATURE";

    private HiddenField amountField = new HiddenField("amountField");
    private HiddenField currencyField = new HiddenField("currencyField");
    private HiddenField descriptionField = new HiddenField("descriptionField");
    private HiddenField merchantField = new HiddenField("merchantField");
    private HiddenField successURL = new HiddenField("successURL");
    private HiddenField failURL = new HiddenField("failURL");
    private HiddenField paymentId = new HiddenField("paymentId");
    private HiddenField signature = new HiddenField("signature");

    private Label payButtonLabel = new Label("payLabel", new Model<String>());

    protected IModel<PaidEntity> entityModel = new GenericDBModel<>(PaidEntity.class);

    protected IModel<PaidEntity> entity2Model = new GenericDBModel<>(PaidEntity.class);

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public PayButton(String id) {
        super(id);
        addComponents();
    }

    /**
     * Setter of payment data.
     *
     * @param entity payment entity.
     */
    public void setPaymentData(PaidEntity entity) {
        entityModel.setObject(entity);
        constructForm();
    }

    /**
     * Setter of payment data (two payments).
     *
     * @param entity1 payment entity 1.
     * @param entity2 payment entity 2.
     */
    public void setPaymentData(PaidEntity entity1, PaidEntity entity2) {
        entityModel.setObject(entity1);
        entity2Model.setObject(entity2);
        constructForm();
    }

    public abstract String getPaymentId();

    public abstract BigDecimal getPrice();

    public abstract String getDescription();

    public String getLabelString() {
        return getString("payLabel");
    }

    protected String getW1Id() {
        return eltilandProps.getProperty("profile.id");
    }

    protected String getW1Hash() {
        return eltilandProps.getProperty("profile.hash");
    }

    protected void constructForm() {
        String description = getDescription();
        BigDecimal price = getPrice();
        String id = getPaymentId();

        amountField.add(new AttributeModifier(PARAM_VALUE, price));
        amountField.add(new AttributeModifier(PARAM_NAME, W1_AMOUNT));

        currencyField.add(new AttributeModifier(PARAM_VALUE, RUB_ID));
        currencyField.add(new AttributeModifier(PARAM_NAME, W1_CURRENCY));

        descriptionField.add(new AttributeModifier(PARAM_VALUE, StringUtils.truncate(description, 250)));

        descriptionField.add(new AttributeModifier(PARAM_NAME, W1_DESCRIPTION));
        merchantField.add(new AttributeModifier(PARAM_VALUE, getW1Id()));
        merchantField.add(new AttributeModifier(PARAM_NAME, W1_MERCHANT));

        successURL.add(new AttributeModifier(PARAM_VALUE, eltilandProps.getProperty("application.base.url") +
                "/paymentSuccess"));
        successURL.add(new AttributeModifier(PARAM_NAME, W1_SUCCESS_URL));

        failURL.add(new AttributeModifier(PARAM_VALUE, eltilandProps.getProperty("application.base.url") +
                "/paymentFail"));
        failURL.add(new AttributeModifier(PARAM_NAME, W1_FAIL_URL));

        paymentId.add(new AttributeModifier(PARAM_VALUE, id));
        paymentId.add(new AttributeModifier(PARAM_NAME, W1_PAYMENT_ID));

        signature.add(new AttributeModifier(PARAM_VALUE,
                getSignature(StringUtils.truncate(description, 250), price.toString(), id)));
        signature.add(new AttributeModifier(PARAM_NAME, W1_SIGNATURE));
    }

    protected String getTimeString() {
        Date currentDate = DateUtils.getCurrentDate();
        return String.valueOf(currentDate.getTime());
    }

    private void addComponents() {
        add(amountField);
        add(currencyField);
        add(descriptionField);
        add(merchantField);
        add(successURL);
        add(failURL);
        add(paymentId);
        add(signature);
        add(payButtonLabel);

        payButtonLabel.setDefaultModelObject(getLabelString());
    }

    private String getSignature(String description, String price, String id) {
        String data = RUB_ID + description +
                eltilandProps.getProperty("application.base.url") + "/paymentFail" +
                getW1Id() + price + id +
                eltilandProps.getProperty("application.base.url") + "/paymentSuccess" +
                getW1Hash();
        String signature = "";
        try {
            byte[] bytesOfMessage = data.getBytes("Windows-1251");
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] result = new byte[md5.getDigestLength()];
            md5.reset();
            md5.update(bytesOfMessage);
            result = md5.digest();

            for (byte s : Base64.encodeBase64(result)) {
                signature += (char) s;
            }

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOGGER.error("Error calculation CRC for payment", e);
            throw new WicketRuntimeException(e);
        }
        return signature;
    }
}
