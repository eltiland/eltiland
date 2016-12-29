package com.eltiland.bl.impl.test;

import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestAttemptManager;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Test attempt manager.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class TestAttemptManagerImpl extends ManagerImpl implements TestAttemptManager {

    @Override
    @Transactional(readOnly = true)
    public UserTestAttempt getAttempt(TestCourseItem item) {
        User user = EltilandSession.get().getCurrentUser();

        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class);
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("test", item));

        return (UserTestAttempt) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAttemptRecord(TestCourseItem item) {
        User user = EltilandSession.get().getCurrentUser();

        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class);
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("test", item));
        return criteria.list().size() != 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int getSuccessCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .setFetchMode("user", FetchMode.JOIN);
        criteria.add(Restrictions.eq("completed", true));
        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTestAttempt> getSuccessList(int index, Integer count, String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .createAlias("user", "user")
                .createAlias("test", "test")
                .createAlias("test.courseFull", "course");
        criteria.add(Restrictions.eq("completed", true));
        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return addSortCriteria(criteria, index, count, sProperty, isAscending).list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getProcessCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .setFetchMode("user", FetchMode.JOIN);
        criteria.add(Restrictions.eq("completed", false));
        criteria.add(Restrictions.sqlRestriction("attempt_count < attempt_limit"));
        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTestAttempt> getProcessList(int index, Integer count, String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .createAlias("user", "user")
                .createAlias("test", "test")
                .createAlias("test.courseFull", "course");
        criteria.add(Restrictions.eq("completed", false));
        criteria.add(Restrictions.sqlRestriction("attempt_count < attempt_limit"));

        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }

        return addSortCriteria(criteria, index, count, sProperty, isAscending).list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getLimitCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .setFetchMode("user", FetchMode.JOIN);
        criteria.add(Restrictions.eq("completed", false));
        criteria.add(Restrictions.sqlRestriction("attempt_count >= attempt_limit"));
        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTestAttempt> getLimitList(int index, Integer count, String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class)
                .createAlias("user", "user")
                .createAlias("test", "test")
                .createAlias("test.courseFull", "course");
        criteria.add(Restrictions.eq("completed", false));
        criteria.add(Restrictions.sqlRestriction("attempt_count >= attempt_limit"));

        if (searchString != null) {
            criteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }

        return addSortCriteria(criteria, index, count, sProperty, isAscending).list();
    }

    @Override
    @Transactional(readOnly = true)
    public UserTestAttempt checkResult(User user, TestCourseItem test) {
        Criteria criteria = getCurrentSession().createCriteria(UserTestAttempt.class);

        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("test", test));
        return (UserTestAttempt) criteria.uniqueResult();
    }

    private Criteria addSortCriteria(
            Criteria criteria, int index, Integer count, String sProperty, boolean isAscending) {
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria;
    }
}
