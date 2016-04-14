package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.CoursePrintStatManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseItemPrintStatValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Course item print statistics manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CoursePrintStatManagerImpl extends ManagerImpl implements CoursePrintStatManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private CourseItemPrintStatValidator courseItemPrintStatValidator;

    @Override
    @Transactional
    public CourseItemPrintStat create(ELTCourseListener listener, ELTGoogleCourseItem item) throws CourseException {
        CourseItemPrintStat existedStat = getItem(listener, item);
        if (existedStat != null) {
            return existedStat;
        }

        CourseItemPrintStat stat = new CourseItemPrintStat();
        stat.setListener(listener);
        stat.setItem(item);
        Long limit = ((ELTDocumentCourseItem) item).getLimit();
        stat.setPrintLimit((limit != null) ? limit : (long) 0);
        stat.setCurrentPrint((long) 0);

        courseItemPrintStatValidator.isValid(stat);
        try {
            genericManager.saveNew(stat);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_CREATE, e);
        }
        return stat;
    }

    @Override
    @Transactional
    public CourseItemPrintStat update(CourseItemPrintStat stat) throws CourseException {
        courseItemPrintStatValidator.isValid(stat);
        try {
            genericManager.update(stat);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_UPDATE, e);
        }
        return stat;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseItemPrintStat getItem(ELTCourseListener listener, ELTGoogleCourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(CourseItemPrintStat.class);
        criteria.add(Restrictions.eq("listener", listener));
        criteria.add(Restrictions.eq("item", item));
        return (CourseItemPrintStat) criteria.uniqueResult();
    }
}
