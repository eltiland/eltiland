package com.eltiland.bl.impl;

import com.eltiland.bl.FaqCategoryManager;
import com.eltiland.bl.FaqManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.validators.FaqValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.FaqCategory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implemenetation of the manager for FAQ category entity.
 *
 * @author Pavel Androschuk
 */

@Component
public class FaqCategoryManagerImpl extends ManagerImpl implements FaqCategoryManager {
    @Autowired
    private FaqValidator faqValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public FaqCategory getById(long id) {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class)
                .add(Restrictions.eq("id", id));
        return (FaqCategory) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqCategory> getList() {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class)
                .addOrder(Order.asc("number"));
        return criteria.list();
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public FaqCategory create(FaqCategory item) throws FaqException {
        item.setNumber(getCurrentMaxNumber() + 1);
        faqValidator.validateCategory(item);
        try {
            genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.CREATE_CATEGORY_ERROR);
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class);
        if (searchString != null) {
            criteria.add(Restrictions.like("name", "%" + searchString + "%"));
        }

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqCategory> getList(int index, Integer count, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class);
        if (searchString != null) {
            criteria.add(Restrictions.like("name", "%" + searchString + "%"));
        }
        criteria.addOrder(Order.asc("number"));

        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void moveUp(FaqCategory item) throws FaqException {
        int oldNumber = item.getNumber();
        if (oldNumber != 1) {
            FaqCategory prevItem = getFaqCategoryByNumber(oldNumber - 1);
            prevItem.setNumber(oldNumber);
            item.setNumber(oldNumber - 1);

            try {
                genericManager.update(prevItem);
                genericManager.update(item);
            } catch (ConstraintException e) {
                throw new FaqException(FaqException.CHANGE_CATEGORY_ORDER_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void moveDown(FaqCategory item) throws FaqException {
        int oldNumber = item.getNumber();
        if (oldNumber != getCount(null)) {
            FaqCategory nextItem = getFaqCategoryByNumber(oldNumber + 1);
            nextItem.setNumber(oldNumber);
            item.setNumber(oldNumber + 1);

            try {
                genericManager.update(nextItem);
                genericManager.update(item);
            } catch (ConstraintException e) {
                throw new FaqException(FaqException.CHANGE_CATEGORY_ORDER_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void delete(FaqCategory item) throws FaqException {
        int oldNumber = item.getNumber();

        int currentNumber;
        for (currentNumber = oldNumber; currentNumber < getCount(null); currentNumber++) {
            moveUp(getFaqCategoryByNumber(currentNumber + 1));
        }

        try {
            genericManager.delete(getFaqCategoryByNumber(currentNumber));
        } catch (EltilandManagerException e) {
            throw new FaqException(FaqException.DELETE_CATEGORY_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void update(FaqCategory item) throws FaqException {
        faqValidator.validateCategory(item);
        try {
            genericManager.update(item);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.EDIT_CATEGORY_ERROR);
        }
    }

    @Transactional(readOnly = true)
    private int getCurrentMaxNumber() {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class)
                .setProjection(Projections.max("number"));
        Object result = criteria.uniqueResult();
        return (result == null) ? 0 : ((Integer) result).intValue();
    }

    @Transactional(readOnly = true)
    private FaqCategory getFaqCategoryByNumber(int number) {
        Criteria criteria = getCurrentSession().createCriteria(FaqCategory.class)
                .add(Restrictions.eq("number", number));
        return (FaqCategory) criteria.uniqueResult();
    }
}