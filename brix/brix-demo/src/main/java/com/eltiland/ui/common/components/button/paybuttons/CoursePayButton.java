package com.eltiland.ui.common.components.button.paybuttons;

import java.math.BigDecimal;

/**
 * Button for course paying.
 *
 * @author Aleksey Plotnikov
 */
public class CoursePayButton extends PayButton {
    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public CoursePayButton(String id) {
        super(id);
    }

    @Override
    public String getPaymentId() {
        return "C" + entityModel.getObject().getPaidId().toString() + "#" + getTimeString();
    }

    @Override
    public BigDecimal getPrice() {
        return entityModel.getObject().getPrice();
    }

    @Override
    public String getDescription() {
        return getString("coursePayDescription");
    }
}
