package com.eltiland.bl.user.impl;

import com.eltiland.bl.*;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.validators.UserValidator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.model.user.*;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 */
@Component
public class UserManagerImpl extends ManagerImpl implements UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerImpl.class);

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private ConfirmationManager confirmationManager;

    @Autowired
    private EmailMessageManager emailMessageManager;

    @Autowired
    private GenericManager genericManager;

    @Autowired
    private PeiManager peiManager;

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;


    @Override
    @Transactional(readOnly = true)
    public User initializeSimpleUserInfo(User user) {
        Query q = getCurrentSession()
                .createQuery("select distinct user from User as user"
                        + " left join fetch user.avatar as avatar"
                        + " left join fetch avatar.previewBody as preview"
                        + " left join fetch user.authCourses as authCourses"
                        + " left join fetch user.courses as courses"
                        + " where user.id = :id");
        q.setParameter("id", user.getId());

        return (User) q.uniqueResult();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User createUser(User toCreate) throws UserException, EltilandManagerException {
        userValidator.validateCreateParams(toCreate);

        if (toCreate.getAvatar() != null) {
            if (toCreate.getAvatar().getId() == null) {
                try {
                    fileManager.saveFile(toCreate.getAvatar());
                } catch (FileException e) {
                    throw new EltilandManagerException(e.getMessage(), e);
                }
            }
        }
        toCreate.setConfirmationDate(DateUtils.getCurrentDate());
        toCreate.setActive(true);
        getCurrentSession().persist(toCreate);

        return toCreate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updateUser(User toUpdate) throws UserException {
        userValidator.validateCreateParams(toUpdate);

        if (toUpdate.getAvatar() != null) {
            if (toUpdate.getAvatar().getId() == null) {
                try {
                    fileManager.saveFile(toUpdate.getAvatar());
                } catch (FileException e) {
                    throw new UserException(UserException.ERROR_USER_UPDATE, e);
                }
            }
        }

        // update settings for current user
        if (EltilandSession.get().getCurrentUser() != null
                && EltilandSession.get().getCurrentUser().getId().equals(toUpdate.getId())) {
            EltilandSession.get().updateCurrentUser(toUpdate);
        }
        return (User) getCurrentSession().merge(toUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(User toDelete) throws EltilandManagerException {
        genericManager.delete(toDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        Query query = getCurrentSession().createQuery("select user from User as user where user.id = :id")
                .setParameter("id", id);
        return (User) query.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return getUserByClass(email, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    @Transactional(readOnly = true)
    public User getUserByClass(String email, Class<? extends User> clazz) {
        String queryString = "select user from User as user where lower(user.email) = :email";
        if (clazz != null) {
            queryString += " and user.class = :clazz";
        }
        Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("email", email.toLowerCase());
        if (clazz != null) {
            query.setParameter("clazz", clazz.getAnnotation(DiscriminatorValue.class).value());
        }

        return (User) query.uniqueResult();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User initializeAvatarInfo(User user) {
        return (User) getCurrentSession()
                .createQuery("select user from User as user"
                        + " left join fetch user.avatar as avatar"
                        + " left join fetch avatar.previewBody as preview"
                        + " where user.id = :id"
                ).setParameter("id", user.getId()).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserSearchList(int index, int count,
                                        String searchString, String sortProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);

        if( searchString != null ) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.ilike("email", searchString));
            searchCriteria.add(Restrictions.ilike("name", searchString));
            criteria.add(searchCriteria);
        }

        if( sortProperty != null ) {
            criteria.addOrder(isAscending ? Order.asc(sortProperty) : Order.desc(sortProperty));
        }

        criteria.setFirstResult(index);
        criteria.setMaxResults(count);

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserSearchCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);

        if( searchString != null ) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.ilike("email", searchString));
            searchCriteria.add(Restrictions.ilike("name", searchString));
            criteria.add(searchCriteria);
        }

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getCourseListeners(Course course, int first, int count, String sProperty, boolean isAscending) {
        String order = parseOrderCriteria("user", sProperty, isAscending).toString();

        return getCurrentSession()
                .createQuery("select distinct user from User as user"
                        + " left join fetch user.courses as course"
                        + " where course = :course order by " + order
                ).setEntity("course", course)
                .setFirstResult(first)
                .setMaxResults(count)
                .list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCoursePaidListenersCount(Course course) {
        Query query = getCurrentSession()
                .createQuery("select count(user) from User as user"
                        + " left join user.coursePayments as payment"
                        + " left join payment.invoice as invoice"
                        + " left join invoice.course as course"
                        + " where course = :course"
                ).setEntity("course", course);
        return ((Long) query.uniqueResult()).intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getCoursePaidListeners(Course course, int first, int count, String sProperty, boolean isAscending) {
        String order = parseOrderCriteria("user", sProperty, isAscending).toString();

        return getCurrentSession()
                .createQuery("select distinct user from User as user"
                        + " left join fetch user.coursePayments as payment"
                        + " left join fetch payment.invoice as invoice"
                        + " left join fetch invoice.course as course"
                        + " where course = :course and payment.status = true order by " + order
                ).setEntity("course", course)
                .setFirstResult(first)
                .setMaxResults(count)
                .list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCountAvailableToWebinar(String searchString, Webinar webinar) {
        Criteria criteria = getWebinarCriteria(webinar, searchString);
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserListAvailableToWebinar(String searchString, int first, int count, String sProperty,
                                                    boolean isAscending, Webinar webinar) {
        Criteria criteria = getWebinarCriteria(webinar, searchString);
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCountOnWebinars(String searchString, List<Webinar> webinars) {
        return getUserWebinarCriteria(searchString, webinars).list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserListOnWebinars(
            String searchString, int first, int count, String sProperty, boolean isAsc, List<Webinar> webinars) {
        Criteria criteria = getUserWebinarCriteria(searchString, webinars);
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getConfirmedUsersCount(boolean isConfirmed) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);
        if (isConfirmed) {
            criteria.add(Restrictions.isNotNull("confirmationDate"));
        } else {
            criteria.add(Restrictions.isNull("confirmationDate"));
        }
        int count = criteria.list().size();
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersNotSubscribers() {
        Criteria criteria = getCurrentSession().createCriteria(Subscriber.class);
        criteria.setProjection(Projections.property("email"));
        List<String> emails = criteria.list();

        Criteria userCriteria = getCurrentSession().createCriteria(User.class);
        userCriteria.add(Restrictions.not(Restrictions.in("email", emails)));
        return userCriteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUsersAvailiableForSessionCount(CourseSession session, String searchString) throws EltilandManagerException {
        try {
            FullTextQuery query = createUserTrainingSearchFullTextQuery(createTrainingCriteria(session, searchString));
            return query.list().size();
        } catch (IOException | ParseException e) {
            throw new EltilandManagerException("Error while searching by webinar users", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersAvailiableForSession(
            CourseSession session, String searchString, int first, int count, String sProperty, boolean isAsc) throws EltilandManagerException {
        try {
            FullTextQuery query = createUserTrainingSearchFullTextQuery(createTrainingCriteria(session, searchString));
            query.setMaxResults(count);
            query.setFirstResult(first);
            query.setSort(new Sort(new SortField(sProperty, SortField.STRING, isAsc)));
            return query.list();
        } catch (IOException | ParseException e) {
            throw new EltilandManagerException("Error while searching by webinar users", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getInvitedUsersCount(CourseListener listener) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);
        criteria.createAlias("invitorSet", "invitor", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("invitor.id", listener.getId()));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getInvitedUsers(CourseListener listener, int first, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);
        criteria.createAlias("invitorSet", "invitor", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("invitor.id", listener.getId()));
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getCourseAdmins(ELTCourse course, int first, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);
        criteria.createAlias("adminCourses", "admins", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("admins.id", course.getId()));
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Transactional(readOnly = true)
    private Criteria getUserWebinarCriteria(String searchString, List<Webinar> webinars) {

        List<Long> webinarUsers = new ArrayList<>();
        boolean webinarSearch = !(webinars.isEmpty());

        if (webinarSearch) {
            for (Webinar webinar : webinars) {
                List<Long> tList;

                Criteria wPaymentCriteria = getCurrentSession().createCriteria(WebinarUserPayment.class);
                wPaymentCriteria.add(Restrictions.eq("webinar", webinar));
                wPaymentCriteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
                wPaymentCriteria.setProjection(Projections.property("userProfile.id"));
                wPaymentCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                tList = wPaymentCriteria.list();

                Criteria rPaymentCriteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
                rPaymentCriteria.createAlias("record", "record", JoinType.LEFT_OUTER_JOIN);
                rPaymentCriteria.add(Restrictions.eq("record.webinar", webinar));
                rPaymentCriteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
                rPaymentCriteria.setProjection(Projections.property("userProfile.id"));
                rPaymentCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                tList = (List<Long>) CollectionUtils.union(tList, rPaymentCriteria.list());

                webinarUsers = webinarUsers.isEmpty() ?
                        tList : (List<Long>) CollectionUtils.intersection(webinarUsers, tList);
            }
        }

        Criteria criteria = getCurrentSession().createCriteria(User.class);

        if (!webinarSearch) {
            criteria.createAlias("webinarUserPayments", "payments", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("webinarRecordPayments", "recordPayments", JoinType.LEFT_OUTER_JOIN);

            Disjunction webinarCriteria = Restrictions.disjunction();
            webinarCriteria.add(Restrictions.eq("payments.status", PaidStatus.CONFIRMED));
            webinarCriteria.add(Restrictions.eq("recordPayments.status", PaidStatus.CONFIRMED));
            criteria.add(webinarCriteria);
        } else {
            criteria.add(Restrictions.in("id", webinarUsers));
        }

        if (searchString != null) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("email", searchString, MatchMode.ANYWHERE).ignoreCase());
            criteria.add(searchCriteria);
        }

        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria;
    }

    @Transactional(readOnly = true)
    private Criteria getWebinarCriteria(Webinar webinar, String searchString) {
        Criteria subCriteria = getCurrentSession().createCriteria(User.class);
        subCriteria.createAlias("webinarUserPayments", "payments", JoinType.LEFT_OUTER_JOIN);
        subCriteria.createAlias("payments.webinar", "webinar", JoinType.LEFT_OUTER_JOIN);
        subCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        subCriteria.add(Restrictions.eq("webinar.id", webinar.getId()));

        subCriteria.setProjection(Projections.property("id"));
        List<String> ids = subCriteria.list();

        Criteria criteria = getCurrentSession().createCriteria(User.class);
        if (searchString != null) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("email", searchString, MatchMode.ANYWHERE).ignoreCase());
            criteria.add(searchCriteria);
        }

        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (!ids.isEmpty()) {
            criteria.add(Restrictions.not(Restrictions.in("id", ids)));
        }
        return criteria;
    }

    private FullTextQuery createUserSearchFullTextQuery(String searchString)
            throws IOException, ParseException {
        String[] fields = new String[]{
                "name",
                "email"
        };

        //Create a multi-field Lucene query
        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());

        org.apache.lucene.search.Query query = null;

        RussianAnalyzer russianAnalyzer;
        russianAnalyzer = new RussianAnalyzer(Version.LUCENE_36);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, russianAnalyzer);

        if (StringUtils.isEmpty(searchString)) {
            query = parser.parse("(*:*)");
        } else {
            query = parser.parse(QueryParser.escape(searchString));
        }

        FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, User.class);

        return hibQuery;
    }

    private FullTextQuery createUserTrainingSearchFullTextQuery(User.UserTrainingCourseCriteria criteria)
            throws IOException, ParseException, SearchException {
        String[] fields = new String[]{"name", "email"};

        //Create a multi-field Lucene query
        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());

        org.apache.lucene.search.Query query = null;
        RussianAnalyzer russianAnalyzer = new RussianAnalyzer(Version.LUCENE_36);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, russianAnalyzer);

        if (StringUtils.isEmpty(criteria.getSearchQuery())) {
            query = parser.parse("(*:*)");
        } else {
            query = parser.parse(QueryParser.escape(criteria.getSearchQuery()));
        }

        FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, User.class);
        hibQuery.enableFullTextFilter("userTrainingCourseFactory").setParameter("searchCriteria", criteria);

        return hibQuery;
    }

    /**
     * Construct search criteria which will be used by Lucene search engine to find webinar users.
     *
     * @param session course session.
     * @param query   query pattern
     * @return criteria for search
     */
    private User.UserTrainingCourseCriteria createTrainingCriteria(CourseSession session, String query) {
        User.UserTrainingCourseCriteria searchCriteria = new User.UserTrainingCourseCriteria();

        Criteria listenersCriteria = getCurrentSession().createCriteria(CourseListener.class);
        listenersCriteria.add(Restrictions.eq("session", session));
        List<CourseListener> listeners = listenersCriteria.list();

        List<Long> ids = new ArrayList<>();
        for (CourseListener listener : listeners) {
            ids.add(listener.getListener().getId());
            genericManager.initialize(listener, listener.getUsers());
            for (User user : listener.getUsers()) {
                ids.add(user.getId());
            }
        }

        searchCriteria.setIds(ids);
        searchCriteria.setSearchQuery(query);
        return searchCriteria;
    }
}
