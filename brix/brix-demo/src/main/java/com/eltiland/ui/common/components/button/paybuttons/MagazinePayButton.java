package com.eltiland.ui.common.components.button.paybuttons;

import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * Button for magazine paying.
 *
 * @author Aleksey Plotnikov
 */
public class MagazinePayButton extends PayButton {

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public MagazinePayButton(String id) {
        super(id);
    }

    @Override
    public String getPaymentId() {
        return "M" + entityModel.getObject().getPaidId().toString() + "#" + getTimeString();
    }

    @Override
    public BigDecimal getPrice() {
        return entityModel.getObject().getPrice();
    }

    @Override
    public String getDescription() {
        return getString("magazinePayDescription");
    }

    @Override
    protected String getW1Id() {
        return eltilandProps.getProperty("profile.magazine.id");
    }

    @Override
    protected String getW1Hash() {
        return eltilandProps.getProperty("profile.magazine.hash");
    }
}
