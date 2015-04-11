package com.eltiland.model.payment;

import com.eltiland.model.Identifiable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity with price
 *
 * @author Aleksey Plotnikov.
 */
public interface PaidEntityNew extends Identifiable {

    /**
     * Returns the name of the entity.
     *
     * @return name of the entity.
     */
    public String getEntityName();

    /**
     * Returns the name of the user.
     *
     * @return name of the user.
     */
    public String getUserName();

    /**
     * Returns the email of the user.
     *
     * @return email of the user.
     */
    public String getUserEmail();

    /**
     * Return description of the payment.
     *
     * @return email of the user.
     */
    public String getDescription();

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
    public PaidStatus getStatus();

    /**
     * Status setter.
     *
     * @param status status to set.
     */
    public void setStatus(PaidStatus status);

    /**
     * Setter for payment date
     *
     * @param date payment date.
     */
    public void setPayDate(Date date);
}
