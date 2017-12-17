package com.eltiland.bl.impl;

import com.eltiland.bl.WebinarRecordManager;
import com.eltiland.model.webinar.WebinarRecord;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Class for managing Webinars.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarRecordManagerImpl extends ManagerImpl implements WebinarRecordManager {

    @Override
    @Transactional(readOnly = true)
    public int getCount(Boolean isCourse) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarRecord.class);
        if (isCourse != null && isCourse) {
            criteria.createAlias("webinar", "webinar", JoinType.LEFT_OUTER_JOIN);
            criteria.add(Restrictions.eq("webinar.course", isCourse));
        }
        criteria.add(Restrictions.eq("open", true));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarRecord> getList(
            int index, Integer count, String sProperty, boolean isAscending, Boolean isCourse) {

        Criteria criteria = getCurrentSession().createCriteria(WebinarRecord.class);
        criteria.setFetchMode("webinar", FetchMode.JOIN);
        criteria.createAlias("webinar", "webinar", JoinType.LEFT_OUTER_JOIN);
        if (isCourse != null && isCourse) {
            criteria.add(Restrictions.eq("webinar.course", isCourse));
        }
        criteria.add(Restrictions.eq("open", true));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        if (sProperty.equals("id")) {
            criteria.addOrder(Order.desc("webinar.startDate"));
        } else {
            criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        }
        return criteria.list();
    }
}
