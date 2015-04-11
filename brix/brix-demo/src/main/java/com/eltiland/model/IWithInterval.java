package com.eltiland.model;

import java.util.Date;

/**
 * Interface for items, which have date interval.
 *
 * @author Aleksey Plotnikov.
 */
public interface IWithInterval {
    /**
     * Start date getter.
     *
     * @return start date
     */
    public Date getStartDate();

    /**
     * Start date setter
     *
     * @param startDate start date.
     */
    public void setStartDate(Date startDate);

    /**
     * End date getter.
     *
     * @return end date.
     */
    public Date getEndDate();

    /**
     * End date setter.
     *
     * @param endDate end date.
     */
    public void setEndDate(Date endDate);
}
