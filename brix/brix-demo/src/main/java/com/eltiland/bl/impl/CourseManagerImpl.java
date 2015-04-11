package com.eltiland.bl.impl;

import com.eltiland.bl.*;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CourseManagerImpl extends ManagerImpl implements CourseManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private CourseSessionManager courseSessionManager;
    @Autowired
    private CourseListenerManager courseListenerManager;
    @Autowired
    private CourseUserDataManager courseUserDataManager;

    @Override
    @Transactional
    public Course createCourse(Course course) throws EltilandManagerException {
        try {
            course.setStatus(false);
            course.setPublished(false);
            course.setAutoJoin(true);
            course = genericManager.saveNew(course);
            courseUserDataManager.createStandart(course);
            return course;
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when creating course", e);
        }
    }

    @Override
    @Transactional
    public Course createTrainingCourse(Course course, CourseSession session) throws EltilandManagerException {
        course = createCourse(course);
        if (session != null) {
            session.setCourse(course);
            try {
                genericManager.saveNew(session);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint violation when creating course session", e);
            }
        }
        return course;
    }

    @Override
    @Transactional
    public void deleteCourse(Course course) throws EltilandManagerException {
        try {
            genericManager.delete(course);
        } catch (EltilandManagerException e) {
            throw new EltilandManagerException("Constraint violation when deleting course", e);
        }
    }

    @Override
    @Transactional
    public void updateCourse(Course course) throws EltilandManagerException {
        if (course.getIcon() != null) {
            if (course.getIcon().getId() == null) {
                try {
                    fileManager.saveFile(course.getIcon());
                } catch (FileException e) {
                    throw new EltilandManagerException(e.getMessage(), e);
                }
            }
        }

        try {
            genericManager.update(course);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating course", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCourseInvoice(User user) {
        Query query = getCurrentSession().createQuery("select count(course) from Course as course " +
                "where course.author = :user and " +
                "course.status = false")
                .setParameter("user", user);

        return (((Long) query.uniqueResult()).intValue() != 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getCourseList(boolean status, int index, Integer count, String sProperty, boolean isAscending) {
        String order = parseOrderCriteria("course", sProperty, isAscending).toString();

        Query query = getCurrentSession().createQuery("select course from Course as course " +
                "left join fetch course.author as author " +
                "where course.status = :status " +
                "order by " + " " + order)
                .setParameter("status", status);

        if (count != null) {
            query.setMaxResults(count);
        }
        return query.setFirstResult(index).list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCourseListCount(boolean status) {
        Criteria criteria = getCurrentSession().createCriteria(Course.class);
        criteria.add(Restrictions.eq("status", status));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getApprovedCourseList(User user, int index, Integer count, Boolean isTraining) {
        Criteria criteria = getCurrentSession().createCriteria(Course.class);
        criteria.add(Restrictions.eq("author", user));
        criteria.add(Restrictions.eq("status", true));
        if (isTraining != null) {
            criteria.add(Restrictions.eq("training", isTraining));
        }
        criteria.setFirstResult(index);
        if (count != null) {
            criteria.setMaxResults(count);
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getPublishedCourseList(int index, Integer count, Boolean training) {
        Criteria criteria = getCurrentSession().createCriteria(Course.class);
        criteria.add(Restrictions.eq("status", true));
        criteria.add(Restrictions.eq("published", true));
        if (training != null) {
            criteria.add(Restrictions.eq("training", training));
        }
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        Query query = getCurrentSession().createQuery("select course from Course as course " +
                "left join fetch course.author as author " +
                "left join fetch course.demoVersion as demo " +
                "left join fetch course.fullVersion as full " +
                "where course.id = :id")
                .setParameter("id", id);
        return (Course) query.uniqueResult();
    }

    @Override
    @Transactional
    public void addPaidListener(CoursePayment payment) throws EltilandManagerException, UserException {
        Query query = getCurrentSession().createQuery("select course from Course as course " +
                "left join fetch course.invoices as invoice " +
                "left join fetch invoice.payments as payments " +
                "where payments = :payment")
                .setParameter("payment", payment);
        Course course = (Course) query.uniqueResult();

        Query query2 = getCurrentSession().createQuery("select user from User as user " +
                "left join fetch user.coursePayments as payments " +
                "left join fetch user.courses as courses " +
                "where payments = :payment")
                .setParameter("payment", payment);
        User listener = (User) query2.uniqueResult();

        if (course != null && listener != null) {
            listener.getCourses().add(course);
            userManager.updateUser(listener);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getUserCourses(User user, Boolean isTraining) {
        List<Course> courseList;
        Criteria criteria = getCurrentSession().createCriteria(Course.class);
        criteria.createAlias("listeners", "users", JoinType.LEFT_OUTER_JOIN);

        criteria.add(Restrictions.eq("users.id", user.getId()));
        if (isTraining != null) {
            criteria.add(Restrictions.eq("training", isTraining));
        }
        courseList = criteria.list();

        if (isTraining) {
            Criteria listenerCriteria = getCurrentSession().createCriteria(CourseListener.class);
            listenerCriteria.add(Restrictions.eq("listener", user));
            List<CourseListener> listeners = listenerCriteria.list();

            for (CourseListener listener : listeners) {
                listener = courseListenerManager.getListenerById(listener.getId());
                CourseSession session = courseSessionManager.getActiveSession(listener.getSession().getCourse());
                if (session.getId().equals(listener.getSession().getId())) {
                    if (!(courseList.contains(listener.getSession().getCourse()))) {
                        courseList.add(listener.getSession().getCourse());
                    }
                }
            }
        }
        return courseList;
    }
}

