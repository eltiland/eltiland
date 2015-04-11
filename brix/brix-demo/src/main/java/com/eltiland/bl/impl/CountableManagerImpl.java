package com.eltiland.bl.impl;

import com.eltiland.bl.CountableManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.Countable;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CountableManagerImpl<T extends AbstractIdentifiable & Countable>
        extends ManagerImpl implements CountableManager<T> {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public T create(T entity) throws CountableException {
        entity.setIndex(getCurrentMaxNumber(entity.getClass()) + 1);
        try {
            return genericManager.saveNew(entity);
        } catch (ConstraintException e) {
            throw new CountableException(CountableException.ERROR_COUNTABLE_NEW);
        }
    }

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public T enumerate(T entity) throws CountableException {
        entity.setIndex(getCurrentMaxNumber(entity.getClass()) + 1);
        try {
            return genericManager.update(entity);
        } catch (ConstraintException e) {
            throw new CountableException(CountableException.ERROR_COUNTABLE_NEW);
        }
    }

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public void delete(T entity) throws CountableException {
        int oldIndex = entity.getIndex();

        int currentIndex;
        for (currentIndex = oldIndex; currentIndex < getEntityCount(entity.getClass()); currentIndex++) {
            moveUp(getEntityByIndex(currentIndex + 1, entity.getClass()));
        }

        try {
            genericManager.delete(entity);
        } catch (EltilandManagerException e) {
            throw new CountableException(CountableException.ERROR_COUNTABLE_DELETE);
        }
    }

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public void pseudoDelete(T entity) throws CountableException {
        int oldIndex = entity.getIndex();

        int currentIndex;
        for (currentIndex = oldIndex + 1; currentIndex < getEntityCount(entity.getClass()); currentIndex++) {
            moveUp(getEntityByIndex(currentIndex, entity.getClass()));
        }

        entity.setIndex(-1);
        try {
            genericManager.update(entity);
        } catch (ConstraintException e) {
            throw new CountableException(CountableException.ERROR_COUNTABLE_DELETE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public T getEntityByIndex(int index, Class<? extends AbstractIdentifiable> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.eq("index", index));
        return (T) criteria.uniqueResult();
    }

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public void moveUp(T entity) throws CountableException {
        int oldIndex = entity.getIndex();

        if (oldIndex != 0) {
            T prevEntity = getEntityByIndex(oldIndex - 1, entity.getClass());
            prevEntity.setIndex(oldIndex);
            entity.setIndex(oldIndex - 1);

            try {
                genericManager.update(prevEntity);
                genericManager.update(entity);
            } catch (ConstraintException e) {
                throw new CountableException(CountableException.ERROR_COUNTABLE_MOVE);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = CountableException.class)
    public void moveDown(T entity) throws CountableException {
        int oldIndex = entity.getIndex();
        int count = getEntityCount(entity.getClass());

        if (oldIndex != count) {
            T nextEntity = getEntityByIndex(oldIndex + 1, entity.getClass());
            nextEntity.setIndex(oldIndex);
            entity.setIndex(oldIndex + 1);

            try {
                genericManager.update(nextEntity);
                genericManager.update(entity);
            } catch (ConstraintException e) {
                throw new CountableException(CountableException.ERROR_COUNTABLE_MOVE);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getEntityCount(Class<? extends AbstractIdentifiable> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.ne("index", -1));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getEntityList(Class<? extends AbstractIdentifiable> clazz, int first, int count) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.ne("index", -1));
        criteria.addOrder(Order.asc("index"));
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Transactional(readOnly = true)
    private Integer getCurrentMaxNumber(Class<? extends AbstractIdentifiable> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.setProjection(Projections.max("index"));
        return (Integer) criteria.uniqueResult();
    }
}
