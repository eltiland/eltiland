package com.eltiland.ui.course.components.listeners;

import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for changing current price for the course for user.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePriceChangePanel extends ELTDialogPanel implements IDialogUpdateCallback<CoursePayment> {

    IModel<CoursePayment> coursePaymentIModel = new GenericDBModel<>(CoursePayment.class);

    private PriceField priceField = new PriceField(
            "priceField", new ResourceModel("priceField"), new Model<BigDecimal>());

    private IDialogActionProcessor<CoursePayment> callback;

    public CoursePriceChangePanel(String id) {
        super(id);
        form.add(priceField);
    }

    public void initData(IModel<CoursePayment> paymentIModel) {
        coursePaymentIModel.setObject(paymentIModel.getObject());
        priceField.setValue(coursePaymentIModel.getObject().getPrice());
    }

    @Override
    protected String getHeader() {
        return getString("priceHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            CoursePayment payment = coursePaymentIModel.getObject();
            payment.setPrice(priceField.getPriceValue());
            callback.process(coursePaymentIModel, target);
        }
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<CoursePayment> callback) {
        this.callback = callback;
    }
}
