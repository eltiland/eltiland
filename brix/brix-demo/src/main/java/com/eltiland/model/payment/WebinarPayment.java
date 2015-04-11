package com.eltiland.model.payment;

import com.eltiland.model.webinar.Webinar;

/**
 * Interface for paid webinar entities.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarPayment extends PaidEntity {
    /**
     * Webinar getter.
     *
     * @return webinar param.
     */
    public Webinar getWebinar();

    /**
     * Webinar setter.
     *
     * @param webinar webinar to set.
     */
    public void setWebinar(Webinar webinar);
}
