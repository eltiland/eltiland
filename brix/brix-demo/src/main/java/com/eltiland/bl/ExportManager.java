package com.eltiland.bl;

import com.eltiland.model.export.Exportable;

import java.util.Date;
import java.util.List;

/**
 * Manager for Exportable entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ExportManager<T extends Exportable> {

    /**
     * Get payments list for given period.
     *
     * @param clazz     class of exportable entity
     * @param startDate start date of the period.
     * @param endDate   end date of the period.
     * @return payments list.
     */
    List<T> getPaymentsForPeriod(Class<T> clazz, Date startDate, Date endDate);

    /**
     * Get payments list.
     *
     * @param clazz class of exportable entity
     * @return payments list.
     */
    List<T> getAllPayments(Class<T> clazz);
}
