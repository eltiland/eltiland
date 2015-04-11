package com.eltiland.model.export;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Interface for exportable entites.
 *
 * @author Aleksey Plotnikov.
 */
public interface Exportable {
    /**
     * @return name of user.
     */
    public String getName();

    /**
     * @return price.
     */
    public BigDecimal getPrice();

    /**
     * @return additional description of payment.
     */
    public String getDescription();

    /**
     * @return date of the payment.
     */
    public Date getDate();
}
