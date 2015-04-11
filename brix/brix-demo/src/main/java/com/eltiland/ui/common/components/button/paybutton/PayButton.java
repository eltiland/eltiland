package com.eltiland.ui.common.components.button.paybutton;

import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

/**
 * Assist pay button
 *
 * @author Aleksey Plotnikov.
 */
public class PayButton<T extends PaidEntityNew> extends BaseEltilandPanel<T> {

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private HiddenField merchant_id = new HiddenField("merchant_id");
    private HiddenField order_number = new HiddenField("order_number");
    private HiddenField order_amount = new HiddenField("order_amount");
    private HiddenField lastname = new HiddenField("lastname");
    private HiddenField firstname = new HiddenField("firstname");
    private HiddenField middlename = new HiddenField("middlename");
    private HiddenField email = new HiddenField("email");
    private HiddenField url_ok = new HiddenField("url_ok");
    private HiddenField url_no = new HiddenField("url_no");
    private HiddenField order_comment = new HiddenField("order_comment");

    public PayButton(String id, IModel<T> tiModel) {
        super(id, tiModel);

        String order_id = getModelObject().getPaidId().toString() + "_" +
                String.valueOf(DateUtils.getCurrentDate().getTime());

        merchant_id.add(new AttributeModifier("value", eltilandProps.getProperty("assist.id")));
        merchant_id.add(new AttributeModifier("name", "Merchant_ID"));
        add(merchant_id);

        order_number.add(new AttributeModifier("value", order_id));
        order_number.add(new AttributeModifier("name", "OrderNumber"));
        add(order_number);

        order_amount.add(new AttributeModifier("value", getModelObject().getPrice().toString()));
        order_amount.add(new AttributeModifier("name", "OrderAmount"));
        add(order_amount);

        String[] name_parts = getModelObject().getUserName().split("\\s+");
        if (name_parts.length > 0) {
            lastname.add(new AttributeModifier("value", name_parts[0]));
            lastname.add(new AttributeModifier("name", "Lastname"));
        }
        if (name_parts.length > 1) {
            firstname.add(new AttributeModifier("value", name_parts[1]));
            firstname.add(new AttributeModifier("name", "Firstname"));
        }
        if (name_parts.length > 2) {
            middlename.add(new AttributeModifier("value", name_parts[2]));
            middlename.add(new AttributeModifier("name", "Middlename"));
        }

        add(lastname);
        add(firstname);
        add(middlename);

        email.add(new AttributeModifier("value", getModelObject().getUserEmail()));
        email.add(new AttributeModifier("name", "Email"));
        add(email);

        url_ok.add(new AttributeModifier("value",
                eltilandProps.getProperty("assist.external.url") + "/paySuccess?id=" + order_id));
        url_ok.add(new AttributeModifier("name", "URL_RETURN_OK"));
        add(url_ok);
        url_no.add(new AttributeModifier("value", eltilandProps.getProperty("assist.external.url") + "/payFail"));
        add(url_no);
        url_no.add(new AttributeModifier("name", "URL_RETURN_NO"));

        order_comment.add(new AttributeModifier("value", StringUtils.truncate(getModelObject().getDescription(), 255)));
        order_comment.add(new AttributeModifier("name", "OrderComment"));
        add(order_comment);
    }
}
