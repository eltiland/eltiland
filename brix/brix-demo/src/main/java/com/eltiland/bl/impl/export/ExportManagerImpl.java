package com.eltiland.bl.impl.export;

import com.eltiland.bl.ExportManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.model.export.Exportable;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the export manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ExportManagerImpl<T extends Exportable> extends ManagerImpl implements ExportManager<T> {
    @Override
    @Transactional(readOnly = true)
    public List<T> getPaymentsForPeriod(Class<T> clazz, Date startDate, Date endDate) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);

        criteria.add(Restrictions.eq("status", true));
        criteria.add(Restrictions.between("date", startDate, endDate));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getAllPayments(Class<T> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.eq("status", true));

        return criteria.list();
    }
}
