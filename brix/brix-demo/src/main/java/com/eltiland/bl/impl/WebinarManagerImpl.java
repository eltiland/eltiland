package com.eltiland.bl.impl;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.file.File;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Class for managing Webinars.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarManagerImpl extends ManagerImpl implements WebinarManager {

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    @Autowired
    private GenericManager genericManager;

    @Qualifier("webinarServiceV3Impl")
    @Autowired
    private WebinarServiceManager webinarServiceManager;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private FileManager fileManager;

    @Override
    public void authenticate() throws EltilandManagerException {
        webinarServiceManager.authenticate();
    }

    @Override
    @Transactional
    public Webinar create(Webinar webinar) throws EltilandManagerException, WebinarException {
        if (webinar.isApproved()) {
            webinarServiceManager.createEvent(webinar);
        }
        try {
            genericManager.saveNew(webinar);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create Webinar item - may be it is constraint violation", e);
        }
        return webinar;
    }

    @Override
    @Transactional
    public boolean remove(Webinar webinar) throws EltilandManagerException {
        if (webinar.isApproved()) {
            boolean result = webinarServiceManager.removeWebinar(webinar);
            webinar.setStatus(Webinar.Status.DELETED);
            try {
                genericManager.update(webinar);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Cannot update webinar - constraint violation", e);
            }
            return result;
        } else {
            genericManager.delete(webinar);
            return true;
        }
    }

    @Override
    @Transactional
    public boolean update(Webinar webinar) throws EltilandManagerException {
        boolean result = false;
        if (webinar.isApproved()) {
            result = webinarServiceManager.updateWebinar(webinar);
        }
        try {
            genericManager.update(webinar);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update webinar - constraint violation", e);
        }
        return result;
    }

    @Override
    public void apply(Webinar webinar, WebinarUserPayment moderator) throws EltilandManagerException {
        simpleCreate(webinar);
        simpleAdd(moderator);
    }

    @Transactional
    private void simpleCreate(Webinar webinar) throws EltilandManagerException {
        webinarServiceManager.createWebinar(webinar);
        try {
            genericManager.update(webinar);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot apply webinar - constraint violation", e);
        }
    }

    @Transactional
    private void simpleAdd(WebinarUserPayment payment) throws EltilandManagerException {
        genericManager.initialize(payment, payment.getWebinar());
        webinarServiceManager.addUser(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public int getWebinarCount(boolean isFuture, boolean isApproved, String searchString) {
        Date currentDate = DateUtils.getCurrentDate();

        Criteria criteria = getCurrentSession().createCriteria(Webinar.class);
        criteria.add(Restrictions.ne("status", Webinar.Status.DELETED));
        criteria.add(isFuture ? Restrictions.gt("startDate", currentDate) : Restrictions.lt("startDate", currentDate));
        criteria.add(Restrictions.eq("approved", isApproved));
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("name", text))
                        .add(Restrictions.ilike("description", text))
                );
            }
        }

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Webinar> getWebinarList(
            int index, Integer count, String sProperty, boolean isAscending, boolean isFuture, boolean isApproved,
            String searchString) {
        Date currentDate = DateUtils.getCurrentDate();

        Criteria criteria = getCurrentSession().createCriteria(Webinar.class);
        criteria.add(Restrictions.ne("status", Webinar.Status.DELETED));
        criteria.add(isFuture ? Restrictions.gt("startDate", currentDate) : Restrictions.lt("startDate", currentDate));
        criteria.add(Restrictions.eq("approved", isApproved));
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("name", text))
                        .add(Restrictions.ilike("description", text))
                );
            }
        }
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);

        return criteria.list();
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    @Transactional(readOnly = true)
    public int getWebinarAvailableCount() {
        User currentUser = EltilandSession.get().getCurrentUser();
        String queryString = "select count(webinar) from Webinar as webinar " +
                "where webinar.startDate > :nowDate and webinar.registrationDeadline > :nowDate " +
                "and webinar.status = :webinarStatus and webinar.course = false ";
        if (currentUser != null) {
            queryString += "and webinar not in (" +
                    "select distinct webinar2 from Webinar as webinar2 " +
                    "left join webinar2.webinarUserPayments as payments " +
                    "where webinar2.startDate > :nowDate " +
                    "and webinar2.status = :webinarStatus and payments.userProfile.id = :id)";
        }
        Query query = getCurrentSession().createQuery(queryString).
                setParameter("nowDate", DateUtils.getCurrentDate()).
                setParameter("webinarStatus", Webinar.Status.OPENED);
        if (currentUser != null) {
            query.setParameter("id", currentUser.getId());
        }

        return ((Long) query.uniqueResult()).intValue();
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    @Transactional(readOnly = true)
    public List<Webinar> getWebinarAvailableList(int index, Integer count, String sProperty, boolean isAscending) {
        String order = parseOrderCriteria("webinar", sProperty, isAscending).toString();

        User currentUser = EltilandSession.get().getCurrentUser();
        String queryString = "select distinct webinar from Webinar as webinar " +
                "where webinar.startDate > :nowDate and webinar.registrationDeadline > :nowDate " +
                "and webinar.status = :webinarStatus and webinar.course = false ";
        if (currentUser != null) {
            queryString += "and webinar not in (" +
                    "select distinct webinar2 from Webinar as webinar2 " +
                    "left join webinar2.webinarUserPayments as payments " +
                    "where webinar2.startDate > :nowDate " +
                    "and webinar2.status = :webinarStatus and payments.userProfile.id = :id)";
        }
        queryString += " order by " + " " + order;
        Query query = getCurrentSession().createQuery(queryString).
                setParameter("nowDate", DateUtils.getCurrentDate()).
                setParameter("webinarStatus", Webinar.Status.OPENED);
        if (currentUser != null) {
            query.setParameter("id", currentUser.getId());
        }

        return query.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserWebinarCount() {
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null) {
            return 0;
        } else {
            Query query = getCurrentSession().createQuery("select count(webinar) from Webinar as webinar " +
                    " left join webinar.webinarUserPayments as payments " +
                    " where webinar.startDate > :curDate " +
                    " and payments.userProfile.id = :id " +
                    " and webinar.status != :webinarStatus")
                    .setParameter("curDate", DateUtils.getCurrentDate())
                    .setParameter("id", currentUser.getId())
                    .setParameter("webinarStatus", Webinar.Status.DELETED);
            return ((Long) query.uniqueResult()).intValue();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Webinar> getUserWebinarList(int index, Integer count, String sProperty, boolean isAscending) {
        String order = parseOrderCriteria("webinar", sProperty, isAscending).toString();

        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null) {
            return null;
        } else {
            Query query = getCurrentSession().createQuery("select distinct(webinar) from Webinar as webinar " +
                    " left join fetch webinar.webinarUserPayments as payments " +
                    " where webinar.startDate > :curDate " +
                    " and payments.userProfile.id = :id " +
                    " and webinar.status != :webinarStatus " +
                    " order by " + " " + order)
                    .setParameter("curDate", DateUtils.getCurrentDate())
                    .setParameter("id", currentUser.getId())
                    .setParameter("webinarStatus", Webinar.Status.DELETED);
            return query.setFirstResult(index).setMaxResults(count).list();
        }
    }

    @Override
    @Transactional
    public void closeRegistration(Webinar webinar) throws EltilandManagerException {
        setOpen(webinar, false);
    }

    @Override
    @Transactional
    public void openRegistration(Webinar webinar) throws EltilandManagerException {
        setOpen(webinar, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Webinar> getWebinars(String userEmail) {
        Criteria criteria = getCurrentSession().createCriteria(Webinar.class);
        criteria.createAlias("webinarUserPayments", "payments", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record.payments", "recordPayments", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record.payments.userProfile", "recordUser", JoinType.LEFT_OUTER_JOIN);

        Disjunction searchCriteria = Restrictions.disjunction();
        Conjunction webinarSearch = Restrictions.conjunction();
        webinarSearch.add(Restrictions.eq("payments.userEmail", userEmail));
        webinarSearch.add(Restrictions.eq("payments.status", PaidStatus.CONFIRMED));
        Conjunction recordSearch = Restrictions.conjunction();
        recordSearch.add(Restrictions.eq("recordUser.email", userEmail));
        recordSearch.add(Restrictions.eq("recordPayments.status", PaidStatus.CONFIRMED));
        searchCriteria.add(webinarSearch);
        searchCriteria.add(recordSearch);
        criteria.add(searchCriteria);

        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }


    @Override
    @Transactional
    public void updateFiles(Webinar webinar) throws EltilandManagerException {
        Webinar oldEntity = genericManager.getObject(Webinar.class, webinar.getId());

        Set<File> newFiles = webinar.getFiles();
        for (File file : newFiles) {
            if (file.getId() == null) {
                try {
                    fileManager.saveFile(file);
                } catch (FileException e) {
                    throw new EltilandManagerException(e.getMessage(), e);
                }
            }
        }

        for (File oldFile : fileManager.getFilesOfWebinar(webinar)) {
            if (!(newFiles.contains(oldFile))) {
                oldFile.setWebinar(null);
                try {
                    genericManager.update(oldFile);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint exceptions", e);
                }
                try {
                    fileManager.deleteFile(oldFile);
                } catch (FileException e) {
                    throw new EltilandManagerException(e.getMessage(), e);
                }
            }
        }

        for (File newFile : newFiles) {
            if (!(fileManager.getFilesOfWebinar(webinar).contains(newFile))) {
                newFile.setWebinar(oldEntity);
                try {
                    genericManager.update(newFile);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint exceptions", e);
                }
            }
        }
    }

    private void setOpen(Webinar webinar, boolean isOpen) throws EltilandManagerException {
        webinar.setStatus(isOpen ? Webinar.Status.OPENED : Webinar.Status.CLOSED);
        try {
            genericManager.update(webinar);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update Webinar item - may be it is constraint violation", e);
        }
    }
}
