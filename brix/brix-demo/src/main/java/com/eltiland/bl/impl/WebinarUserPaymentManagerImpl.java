package com.eltiland.bl.impl;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Class for managing Webinar's Users.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarUserPaymentManagerImpl extends ManagerImpl implements WebinarUserPaymentManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private WebinarServiceManager webinarServiceManager;
    @Autowired
    private EmailMessageManager emailMessageManager;

    @Transactional
    private void addNewUser(WebinarUserPayment user) throws EltilandManagerException {
        try {
            genericManager.saveNew(user);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create user", e);
        }
    }

    @Override
    @Transactional
    public WebinarUserPayment update(WebinarUserPayment payment) throws EltilandManagerException {
        try {
            payment = genericManager.update(payment);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update payment", e);
        }
        return payment;
    }

    @Override
    @Transactional
    public boolean createModerator(WebinarUserPayment user) throws EltilandManagerException {
        user.setRole(WebinarUserPayment.Role.MODERATOR);
        user.setStatus(true);
        boolean result = webinarServiceManager.addUser(user);
        if (!result) {
            return false;
        } else {
            addNewUser(user);
        }
        return result;
    }

    @Override
    public boolean createUser(WebinarUserPayment user) throws EltilandManagerException, EmailException {
        user.setRole(WebinarUserPayment.Role.MEMBER);

        boolean result = true;
        if (user.getPrice() == null || user.getPrice().equals(BigDecimal.valueOf(0))) { // free access
            user.setStatus(true);
            result = webinarServiceManager.addUser(user);
        } else { // paid access
            user.setStatus(false);
            user.setPaylink(RandomStringUtils.randomAlphanumeric(10));
            emailMessageManager.sendWebinarInvitationToUser(user);
        }
        if (!result) {
            return false;
        } else {
            addNewUser(user);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAlreadyRegistered(Webinar webinar, String email) {
        Query query = getCurrentSession().createQuery(
                "select count(userPayment) from WebinarUserPayment as userPayment " +
                        "left join userPayment.webinar as webinar " +
                        "where webinar = :webinar and userPayment.userEmail = :email")
                .setParameter("webinar", webinar)
                .setParameter("email", email);
        int count = ((Long) query.uniqueResult()).intValue();
        return (count > 0);
    }

    @Override
    @Transactional
    public void removeUserFromWebinar(Webinar webinar, User user) {
        Query query = getCurrentSession().createQuery(
                "delete from WebinarUserPayment where webinar = :webinar and userProfile = :user")
                .setParameter("webinar", webinar)
                .setParameter("user", user);
        query.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public int getWebinarUserCount(Webinar webinar, String pattern) throws EltilandManagerException {
        try {
            FullTextQuery query = createWebinarUserSearchFullTextQuery(
                    createWebinarUserSearchCriteria(webinar, pattern));
            return query.list().size();
        } catch (IOException | ParseException e) {
            throw new EltilandManagerException("Error while searching by webinar users", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getWebinarConfirmedUserCount(Webinar webinar) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class)
                .add(Restrictions.eq("webinar", webinar))
                .add(Restrictions.eq("status", true));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getWebinarUserList(
            Webinar webinar, int index, Integer count, String sProperty,
            boolean isAscending, boolean status) throws EltilandManagerException {

        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class)
                .add(Restrictions.eq("webinar", webinar))
                .add(Restrictions.eq("status", status));
        if (sProperty != null) {
            criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        }
        criteria.setFirstResult(index);

        if (count != null) {
            criteria.setMaxResults(count);
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getWebinarRealListeners(Webinar webinar) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class)
                .add(Restrictions.eq("webinar", webinar))
                .add(Restrictions.eq("status", true))
                .add(Restrictions.ne("role", WebinarUserPayment.Role.MODERATOR));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getWebinarUserList(
            Webinar webinar, int index, Integer count, String sProperty,
            boolean isAscending, String pattern) throws EltilandManagerException {
        try {
            FullTextQuery query = createWebinarUserSearchFullTextQuery(
                    createWebinarUserSearchCriteria(webinar, pattern));
            query.setMaxResults(count);
            query.setFirstResult(index);
            query.setSort(new Sort(new SortField(sProperty, SortField.STRING, isAscending)));
            return query.list();
        } catch (IOException | ParseException e) {
            throw new EltilandManagerException("Error while searching by webinar users", e);
        }
    }

    @Override
    @Transactional
    public void removeUser(WebinarUserPayment userPayment) throws EltilandManagerException {
        if (userPayment.getStatus()) {
            genericManager.initialize(userPayment, userPayment.getWebinar());
            boolean result = webinarServiceManager.removeUser(userPayment);
            if (!result) {
                throw new EltilandManagerException("Cannot remove user");
            }
        }
        genericManager.delete(userPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarUserPayment getPaymentByLink(String payLink) {
        Query query = getCurrentSession().createQuery(
                "select payment from WebinarUserPayment as payment " +
                        "left join fetch payment.webinar " +
                        "where payment.paylink = :payLink")
                .setParameter("payLink", payLink);
        return (WebinarUserPayment) query.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarUserPayment getWebinarPaymentById(long id) {
        Query query = getCurrentSession().createQuery("select distinct userPayment" +
                " from WebinarUserPayment as userPayment " +
                " left join fetch userPayment.webinar as webinar" +
                " where userPayment.id = :id")
                .setParameter("id", id);

        return (WebinarUserPayment) query.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getWebinarPayments(Webinar webinar) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class);
        criteria.add(Restrictions.eq("webinar", webinar));
        return criteria.list();
    }

    @Override
    @Transactional
    public boolean payWebinarUserPayment(WebinarUserPayment payment) throws EltilandManagerException {
        payment.setStatus(true);

        Date nowDate = DateUtils.getCurrentDate();
        payment.setDate(nowDate);
        boolean result = true;

        // check for date
        genericManager.initialize(payment, payment.getWebinar());
        Webinar webinar = payment.getWebinar();
        if (!(webinar.getStartDate().before(nowDate))) {
            result = webinarServiceManager.addUser(payment);
            if (!result) {
                return false;
            }
        }

        try {
            genericManager.update(payment);
        } catch (ConstraintException e) {
            throw new EltilandManagerException(
                    "Cannot update userPayment - most likely it's constraint violation", e);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getWebinarStatusForUser(Webinar webinar, User user) {
        Query query = getCurrentSession().createQuery("select userPayment.status " +
                "from WebinarUserPayment as userPayment " +
                "where userPayment.webinar = :webinar " +
                "and userPayment.userProfile = :userProfile")
                .setParameter("webinar", webinar).setParameter("userProfile", user);
        return (Boolean) query.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public int getPaidPaymentsCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class)
                .add(Restrictions.eq("status", true))
                .add(Restrictions.isNotNull("price"))
                .add(Restrictions.ne("price", BigDecimal.ZERO));
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.createAlias("webinar", "webinar");
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("userName", text))
                        .add(Restrictions.ilike("userSurname", text))
                        .add(Restrictions.ilike("webinar.name", text))
                        .add(Restrictions.ilike("webinar.managername", text))
                        .add(Restrictions.ilike("webinar.managersurname", text))
                );
            }
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getPaidPaymentsList(int index, Integer count,
                                                        String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class)
                .add(Restrictions.eq("status", true))
                .add(Restrictions.isNotNull("price"))
                .add(Restrictions.ne("price", BigDecimal.ZERO));
        criteria.createAlias("webinar", "webinar");
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("userName", text))
                        .add(Restrictions.ilike("userSurname", text))
                        .add(Restrictions.ilike("webinar.name", text))
                        .add(Restrictions.ilike("webinar.managername", text))
                        .add(Restrictions.ilike("webinar.managersurname", text))
                );
            }
        }
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional
    public WebinarUserPayment updateWebinarUser(WebinarUserPayment payment) throws EltilandManagerException {
        update(payment);
        if (payment.getStatus()) {
            webinarServiceManager.updateUser(getWebinarPaymentById(payment.getId()));
        }
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarUserPayment> getWebinarPayments(
            User userProfile, boolean history, Boolean status) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class, "payment");
        criteria.createAlias("payment.webinar", "webinar");
        criteria.add(Restrictions.eq("userProfile", userProfile));
        criteria.add(Restrictions.ne("webinar.status", Webinar.Status.DELETED));
        criteria.add(history ?
                Restrictions.le("webinar.startDate", DateUtils.getCurrentDate()) :
                Restrictions.ge("webinar.startDate", DateUtils.getCurrentDate()));
        if (history) {
            criteria.add(Restrictions.eq("status", true));
        } else {
            if (status != null) {
                criteria.add(Restrictions.eq("status", status));
            }
        }
        criteria.addOrder(Order.desc("registrationDate"));

        return criteria.list();
    }

    @Override
    public String getLink(WebinarUserPayment payment) throws EltilandManagerException {
        if (!payment.getStatus()) {
            return null;
        }

        if (payment.getWebinarlink() != null) {
            return payment.getWebinarlink();
        }

        genericManager.initialize(payment, payment.getWebinar());

        Map<String, String> datas = webinarServiceManager.getUsersData(payment.getWebinar());
        if (datas != null) {
            String link = datas.get(payment.getUserEmail());
            if (link != null) {
                payment.setWebinarlink(link);
                update(payment);
                return link;
            } else {
                webinarServiceManager.addUser(payment);
                return getLink(payment);
            }
        }
        return null;
    }

    @Override
    public List<WebinarUserPayment> checkWebinarUsers(Webinar webinar) throws EltilandManagerException {

        genericManager.initialize(webinar, webinar.getWebinarUserPayments());
        Map<String, String> datas = webinarServiceManager.getUsersData(webinar);

        List<WebinarUserPayment> resultList = new ArrayList<>();

        if (datas != null) {
            for (WebinarUserPayment payment : webinar.getWebinarUserPayments()) {
                if (payment.getStatus() &&
                        !(datas.containsKey(payment.getUserEmail())) &&
                        !(payment.getRole().equals(WebinarUserPayment.Role.MODERATOR))) {
                    resultList.add(payment);
                }
            }
        }

        return resultList.isEmpty() ? null : resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarUserPayment getPaymentForUser(Webinar webinar, User user) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarUserPayment.class);
        criteria.add(Restrictions.eq("webinar", webinar));
        criteria.add(Restrictions.eq("userProfile", user));
        return (WebinarUserPayment) criteria.uniqueResult();
    }

    private FullTextQuery createWebinarUserSearchFullTextQuery(WebinarUserPayment.WebinarSearchCriteria criteria)
            throws IOException, ParseException, SearchException {
        String[] fields = new String[]{"userName", "userSurname", "patronymic", "userEmail"};

        //Create a multi-field Lucene query
        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());

        org.apache.lucene.search.Query query = null;
        RussianAnalyzer russianAnalyzer = new RussianAnalyzer();
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, russianAnalyzer);

        if (StringUtils.isEmpty(criteria.getSearchQuery())) {
            query = parser.parse("(*:*)");
        } else {
            query = parser.parse(QueryParser.escape(criteria.getSearchQuery()));
        }

        FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, WebinarUserPayment.class);
        hibQuery.enableFullTextFilter("webinarUserFilterFactory").setParameter("searchCriteria", criteria);

        return hibQuery;
    }

    /**
     * Construct search criteria which will be used by Lucene search engine to find webinar users.
     *
     * @param webinar givane webinar.
     * @param query   query pattern
     * @return criteria for search
     */
    private WebinarUserPayment.WebinarSearchCriteria createWebinarUserSearchCriteria(
            Webinar webinar, String query) {
        WebinarUserPayment.WebinarSearchCriteria searchCriteria = new WebinarUserPayment.WebinarSearchCriteria();
        searchCriteria.setWebinar(webinar);
        searchCriteria.setSearchQuery(query);
        return searchCriteria;
    }
}
