package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Identifiable;
import com.eltiland.model.tags.ITagable;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Default implementation of the {@link GenericManager}.
 */
@Component
public class GenericManagerImpl extends ManagerImpl implements GenericManager {
    @Autowired
    private TagEntityManager tagEntityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericManagerImpl.class);

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public <T extends Identifiable> T getObject(Class<T> clazz, Long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public <T extends Identifiable> List<T> getObjects(Class<T> clazz, Collection<Long> ids) {
        return getCurrentSession().createCriteria(clazz).add(Restrictions.in("id", ids)).list();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public void initialize(Object entity, Object proxy) {
        try {
            if (!Hibernate.isInitialized(proxy)) {
                getCurrentSession().buildLockRequest(LockOptions.NONE).lock(entity);
                Hibernate.initialize(proxy);

                //use it for development purpose
                //} else if (!getCurrentSession().contains(proxy)) {
                //LOGGER.warn("Proxy was already initialized, but has not attached to session! {}", proxy);
            }
        } catch (HibernateException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getEntityCount(Class entityClass, String searchPropertyName, String searchQuery) {
        Criteria c = getCurrentSession().createCriteria(entityClass);
        if (StringUtils.hasText(searchQuery)) {
            c.add(Restrictions.ilike(searchPropertyName, searchQuery, MatchMode.ANYWHERE));
        }
        c.setProjection(Projections.rowCount());

        return ((Number) c.uniqueResult()).intValue();

    }


    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public <T extends Identifiable> List<T> getEntityList(Class<T> entityClass, List<String> joins,
                                                          List<Criterion> restrictions,
                                                          String searchPropertyName, String searchQuery, String orderBy,
                                                          Boolean isAscending, Integer first, Integer count) {
        Criteria c = getCurrentSession().createCriteria(entityClass);

        for (String join : joins) {
            c.createAlias(join, join, JoinType.LEFT_OUTER_JOIN);
        }

        if (StringUtils.hasText(searchQuery)) {
            c.add(Restrictions.ilike(searchPropertyName, searchQuery, MatchMode.ANYWHERE));
        }
        for (Criterion restriction : restrictions) {
            c.add(restriction);
        }
        if (first != null && count != null) {
            c.setFirstResult(first);
            c.setMaxResults(count);
        }
        if (isAscending) {
            c.addOrder(Order.asc(orderBy));
        } else {
            c.addOrder(Order.desc(orderBy));
        }

        return c.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public <T extends Identifiable> List<T> getEntityList(Class<T> entityClass, String orderBy) {
        return getEntityList(entityClass, "id", "", orderBy, true, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public <T extends Identifiable> List<T> getEntityList(
            Class<T> entityClass,
            String searchPropertyName,
            String searchQuery,
            String orderBy,
            Boolean isAscending,
            Integer first,
            Integer count) {

        return getEntityList(entityClass, Collections.<String>emptyList(), Collections.<Criterion>emptyList(),
                searchPropertyName, searchQuery, orderBy,
                isAscending, first, count);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public <T extends Identifiable> T saveNew(T newObject) throws ConstraintException {
        try {
            getCurrentSession().save(newObject);
            getCurrentSession().flush();
            getCurrentSession().refresh(newObject);
            return newObject;
        } catch (Exception ex) {
            LOGGER.error("Got hibernate exception. Most likely this is just a constraint violation.", ex);
            throw new ConstraintException("Could not create new entity. ", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public <T extends Identifiable> T update(T objectEdited) throws ConstraintException {
        try {
            objectEdited = (T) getCurrentSession().merge(objectEdited);
            // this is required to force constraint check.
            getCurrentSession().flush();
        } catch (HibernateException ex) {
            LOGGER.trace("Got hibernate exception. Most likely this is just a constraint violation.", ex);
            throw new ConstraintException("Could not update entity. ", ex);
        }
        return objectEdited;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void refresh(Object entity) {
        getCurrentSession().refresh(entity);
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    public void delete(Object entity) throws EltilandManagerException {
        try {
            if (entity instanceof ITagable) {
                tagEntityManager.deleteTagEntityById(((ITagable) entity).getId());
            }

            getCurrentSession().delete(entity);

            //call this to perform actual DB request
            getCurrentSession().flush();
            getCurrentSession().clear();
        } catch (HibernateException e) {
            LOGGER.error(String.format("Cannot delete entity! %s", e.toString()));
            throw new EltilandManagerException("Cannot delete entity!", e);
        }
    }
}
