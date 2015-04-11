package com.eltiland.bl.impl;

import com.eltiland.bl.FaqManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.validators.FaqValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqCategory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implemenetation of the manager for FAQ entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class FaqManagerImpl extends ManagerImpl implements FaqManager {
    @Autowired
    private FaqValidator faqValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public List<Faq> findByText(FaqCategory category, String text) {
        if (text == null) {
            return getFaqList(category);
        }

        if (text.isEmpty()) {
            return getFaqList(category);
        }

        Criteria criteria = getCurrentSession().createCriteria(Faq.class);
        criteria.add(Restrictions.eq("category", category));
        criteria.add(Restrictions.disjunction()
                .add(Restrictions.ilike("answer", "%" + text + "%"))
                .add(Restrictions.ilike("question", "%" + text + "%"))
        );
        criteria.addOrder(Order.asc("number"));
        return criteria.list();
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public Faq create(Faq toCreate) throws FaqException {
        toCreate.setNumber(getCurrentMaxNumber(toCreate.getCategory()) + 1);
        faqValidator.validateFaq(toCreate);
        try {
            genericManager.saveNew(toCreate);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.CREATE_FAQ_ERROR);
        }
        return toCreate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Faq> getFaqList(FaqCategory category) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("category", category));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getFaqCount(FaqCategory category, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("category", category));
        if (searchString != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.like("question", "%" + searchString + "%"))
                    .add(Restrictions.like("answer", "%" + searchString + "%")));
        }

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Faq> getFaqList(FaqCategory category, int index, Integer count, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("category", category));
        if (searchString != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.like("question", "%" + searchString + "%"))
                    .add(Restrictions.like("answer", "%" + searchString + "%")));
        }
        criteria.addOrder(Order.asc("number"));

        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void moveUp(Faq faq) throws FaqException {
        int oldNumber = faq.getNumber();
        if (oldNumber != 1) {
            Faq prevFaq = getFaqByNumber(faq.getCategory(), oldNumber - 1);
            prevFaq.setNumber(oldNumber);
            faq.setNumber(oldNumber - 1);

            try {
                genericManager.update(prevFaq);
                genericManager.update(faq);
            } catch (ConstraintException e) {
                throw new FaqException(FaqException.CHANGE_FAQ_ORDER_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void moveDown(Faq faq) throws FaqException {
        int oldNumber = faq.getNumber();
        if (oldNumber != getFaqCount(faq.getCategory(), null)) {
            Faq nextFaq = getFaqByNumber(faq.getCategory(), oldNumber + 1);
            nextFaq.setNumber(oldNumber);
            faq.setNumber(oldNumber + 1);

            try {
                genericManager.update(nextFaq);
                genericManager.update(faq);
            } catch (ConstraintException e) {
                throw new FaqException(FaqException.CHANGE_FAQ_ORDER_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void delete(Faq faq) throws FaqException {
        int oldNumber = faq.getNumber();

        int currentNumber;
        for (currentNumber = oldNumber; currentNumber < getFaqCount(faq.getCategory(), null); currentNumber++) {
            moveUp(getFaqByNumber(faq.getCategory(), currentNumber + 1));
        }

        try {
            genericManager.delete(getFaqByNumber(faq.getCategory(), currentNumber));
        } catch (EltilandManagerException e) {
            throw new FaqException(FaqException.DELETE_FAQ_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void update(Faq faq) throws FaqException {
        faqValidator.validateFaq(faq);
        try {
            genericManager.update(faq);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.EDIT_FAQ_ERROR);
        }
    }

    @Transactional(readOnly = true)
    private int getCurrentMaxNumber(FaqCategory category) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("category", category))
                .setProjection(Projections.max("number"));

        Object result = criteria.uniqueResult();
        return (result == null) ? 0 : ((Integer) result).intValue();
    }

    @Transactional(readOnly = true)
    private Faq getFaqByNumber(FaqCategory category, int number) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("category", category))
                .add(Restrictions.eq("number", number));
        return (Faq) criteria.uniqueResult();
    }
}
