package com.eltiland.bl.course.impl;

import com.eltiland.bl.CountableManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.course.ELTCourseUserDataManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseValidator;
import com.eltiland.exceptions.*;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Course manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseManagerImpl extends ManagerImpl implements ELTCourseManager {

    @Autowired
    private CourseValidator courseValidator;
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private ELTCourseUserDataManager eltCourseUserDataManager;
    @Autowired
    private CountableManager<AuthorCourse> countableManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourse create(ELTCourse course) throws CourseException {
        courseValidator.isCourseValid(course);
        try {
            course = genericManager.saveNew(course);
            eltCourseUserDataManager.createStandart(course);
            return course;
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_COURSE_CREATE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void delete(ELTCourse course) throws CourseException {
        try {
            eltCourseUserDataManager.deleteForCourse(course);
            genericManager.delete(course);
        } catch (EltilandManagerException e) {
            throw new CourseException(CourseException.ERROR_COURSE_REMOVE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourse update(ELTCourse course) throws CourseException {
        courseValidator.isCourseValid(course);
        if (course.getIcon() != null) {
            if (course.getIcon().getId() == null) {
                try {
                    fileManager.saveFile(course.getIcon());
                } catch (FileException e) {
                    throw new CourseException(CourseException.ERROR_COURSE_UPDATE);
                }
            }
        }
        try {
            return genericManager.update(course);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_COURSE_UPDATE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourse publish(ELTCourse course) throws CourseException {
        course.setStatus(CourseStatus.PUBLISHED);
        if (course instanceof AuthorCourse) {
            try {
                countableManager.enumerate((AuthorCourse) course);
            } catch (CountableException e) {
                throw new CourseException(CourseException.ERROR_COURSE_UPDATE);
            }
        }
        return update(course);
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourse unPublish(ELTCourse course) throws CourseException {
        course.setStatus(CourseStatus.CONFIRMED);
        if (course instanceof AuthorCourse) {
            try {
                countableManager.pseudoDelete((AuthorCourse) course);
            } catch (CountableException e) {
                throw new CourseException(CourseException.ERROR_COURSE_UPDATE);
            }
        }
        return update(course);
    }

    @Override
    @Transactional(readOnly = true)
    public ELTCourse getCourseByName(String name) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourse.class);
        criteria.add(Restrictions.eq("name", name));
        return (ELTCourse) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasInvoices() {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourse.class);
        criteria.add(Restrictions.eq("status", CourseStatus.NEW));
        criteria.add(Restrictions.eq("author", EltilandSession.get().getCurrentUser()));
        return criteria.list().size() != 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCourseListCount(List<CourseStatus> statuses) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourse.class);
        Disjunction statusCriteria = Restrictions.disjunction();
        for (CourseStatus status : statuses) {
            statusCriteria.add(Restrictions.eq("status", status));
        }
        criteria.add(statusCriteria);
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourse> getAdminCourses(User user, Class<? extends ELTCourse> clazz) {
        Criteria criteria = getCurrentSession().createCriteria((clazz != null) ? clazz : ELTCourse.class);
        criteria.createAlias("admins", "admins", JoinType.LEFT_OUTER_JOIN);
        Disjunction authorCriteria = Restrictions.disjunction();
        authorCriteria.add(Restrictions.eq("author.id", user.getId()));
        authorCriteria.add(Restrictions.eq("admins.id", user.getId()));
        criteria.add(authorCriteria);
        criteria.add(Restrictions.ne("status", CourseStatus.NEW));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends ELTCourse> getListenerCourses(User user, Class<? extends ELTCourse> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("course", "course", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("listener.id", user.getId()));
        List<ELTCourseListener> listeners = criteria.list();
        List<ELTCourse> courses = new ArrayList<>();
        for (ELTCourseListener listener : listeners) {
            if (clazz != null) {
                if ((clazz.equals(TrainingCourse.class) && (listener.getCourse() instanceof TrainingCourse)) ||
                        (clazz.equals(AuthorCourse.class) && (listener.getCourse() instanceof AuthorCourse))) {
                    courses.add(listener.getCourse());
                }
            } else {
                courses.add(listener.getCourse());
            }
        }
        return courses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorCourse> getSortedAuthorCourses(int index, int count) {
        Criteria criteria = getCurrentSession().createCriteria(AuthorCourse.class);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.add(Restrictions.eq("status", CourseStatus.PUBLISHED));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(Order.asc("index"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorCourse> getAuthorCourses(int index, int count, Boolean isModule) {
        Criteria criteria = getCurrentSession().createCriteria(AuthorCourse.class);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.add(Restrictions.eq("status", CourseStatus.PUBLISHED));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);

        if( isModule != null ) {
            criteria.add(Restrictions.eq("module", isModule));
        }
        criteria.addOrder(Order.desc("id"));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingCourse> getActiveTrainingCourses() {
        Criteria criteria = getCurrentSession().createCriteria(TrainingCourse.class);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.add(Restrictions.gt("finishDate", DateUtils.getCurrentDate()));
        criteria.add(Restrictions.eq("status", CourseStatus.PUBLISHED));
        criteria.addOrder(Order.asc("startDate"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getAuthorCoursesCount(Boolean isModule) {
        Criteria criteria = getCurrentSession().createCriteria(AuthorCourse.class);

        if( isModule != null ) {
            criteria.add(Restrictions.eq("module", isModule));
        }
        criteria.add(Restrictions.eq("status", CourseStatus.PUBLISHED));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingCourse> getPastTrainingCourses() {
        Criteria criteria = getCurrentSession().createCriteria(TrainingCourse.class);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.add(Restrictions.lt("finishDate", DateUtils.getCurrentDate()));
        criteria.add(Restrictions.eq("status", CourseStatus.PUBLISHED));
        criteria.addOrder(Order.desc("startDate"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingCourse fetchDocuments(Long id) {
        Criteria criteria = getCurrentSession().createCriteria(TrainingCourse.class);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.setFetchMode("physicalDoc", FetchMode.JOIN);
        criteria.setFetchMode("legalDoc", FetchMode.JOIN);
        criteria.add(Restrictions.eq("id", id));
        return (TrainingCourse) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourse> getCourseList(
            List<CourseStatus> statuses, int index, Integer count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourse.class);
        Disjunction statusCriteria = Restrictions.disjunction();
        for (CourseStatus status : statuses) {
            statusCriteria.add(Restrictions.eq("status", status));
        }
        criteria.add(statusCriteria);
        criteria.setFetchMode("author", FetchMode.JOIN);
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
