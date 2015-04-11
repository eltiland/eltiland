package com.eltiland.bl.impl;

import com.eltiland.bl.FaqApprovalManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.validators.FaqValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqApproval;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
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
public class FaqApprovalManagerImpl extends ManagerImpl implements FaqApprovalManager {
    @Autowired
    private FaqValidator faqValidator;

    @Autowired
    private GenericManager genericManager;


    @Override
    @Transactional(readOnly = true)
    public boolean isExists(FaqApproval faqApproval) {
        Criteria criteria = getCurrentSession().createCriteria(Faq.class)
                .add(Restrictions.eq("answer", faqApproval.getAnswer()))
                .add(Restrictions.eq("question", faqApproval.getQuestion()));
        return criteria.list().size() > 0;
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void update(FaqApproval item) throws FaqException {
        faqValidator.validateFaqApproval(item);
        try {
            genericManager.update(item);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.EDIT_FAQ_APPROVAL_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public FaqApproval create(FaqApproval toCreate) throws FaqException {
        faqValidator.validateFaqApproval(toCreate);
        try {
            genericManager.saveNew(toCreate);
        } catch (ConstraintException e) {
            throw new FaqException(FaqException.CREATE_FAQ_APPROVAL_ERROR);
        }
        return toCreate;
    }

    @Override
    @Transactional(rollbackFor = FaqException.class)
    public void delete(FaqApproval toDelete) throws FaqException {
        try {
            genericManager.delete(toDelete);
        } catch (EltilandManagerException e) {
            throw new FaqException(FaqException.DELETE_FAQ_APPROVAL_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getFaqApprovalCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(FaqApproval.class);
        if (searchString != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.like("question", "%" + searchString + "%"))
                    .add(Restrictions.like("answer", "%" + searchString + "%")));
        }

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqApproval> getFaqApprovalList(int index, Integer count, String sProperty, boolean isAscending,
                                                String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(FaqApproval.class);
        if (searchString != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.like("question", "%" + searchString + "%"))
                    .add(Restrictions.like("answer", "%" + searchString + "%")));
        }

        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));

        return criteria.setFirstResult(index).setMaxResults(count).list();
    }
}
