package com.eltiland.model.payment;

import com.eltiland.model.Identifiable;

import java.math.BigDecimal;

/**
 * Entity with price
 *
 * @author Aleksey Plotnikov.
 */
public interface PaidEntity extends Identifiable {

    /**
     * Getter for id of the paidentity.
     *
     * @return ID value.
     */
    public Long getPaidId();

    /**
     * Price getter.
     *
     * @return price value.
     */
    public BigDecimal getPrice();

    /**
     * Price setter.
     *
     * @param price price to set.
     */
    void setPrice(BigDecimal price);

    /**
     * Status getter.
     *
     * @return status value.
     */
    public boolean getStatus();

    /**
     * Status setter.
     *
     * @param status status to set.
     */
    public void setStatus(boolean status);
}
