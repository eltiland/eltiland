package com.eltiland.bl.impl.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestVariantManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestVariant;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Test Variant Manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class TestVariantManagerImpl extends ManagerImpl implements TestVariantManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public List<TestVariant> getVariantsForItem(TestCourseItem item) {
        Query query = getCurrentSession().createQuery("select variant from TestVariant as variant " +
                "where variant.item.id = :id order by variant.orderNumber asc")
                .setParameter("id", item.getId());
        return query.list();
    }

    @Override
    @Transactional
    public List<TestVariant> getVariantsForQuestion(TestQuestion question) {
        Query query = getCurrentSession().createQuery("select variant from TestVariant as variant " +
                "where variant.question.id = :id order by variant.orderNumber asc")
                .setParameter("id", question.getId());
        return query.list();
    }

    @Override
    @Transactional
    public void moveVariantOfItem(TestVariant variant, TestCourseItem item, boolean direction)
            throws EltilandManagerException {
        int index = variant.getOrderNumber();
        genericManager.initialize(item, item.getVariants());
        int totalCount = item.getVariants().size();

        if (canBeMoved(direction, index, totalCount)) {
            TestVariant variant2;
            if (direction) {
                variant2 = getVariantOfItemByIndex(item, index - 1);
            } else {
                variant2 = getVariantOfItemByIndex(item, index + 1);
            }
            try {
                swapVariants(variant, variant2);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint exception while move item", e);
            }
        }

    }

    @Override
    @Transactional
    public void moveVariantOfQuestion(TestVariant variant, TestQuestion question, boolean direction)
            throws EltilandManagerException {
        int index = variant.getOrderNumber();
        genericManager.initialize(question, question.getVariants());
        int totalCount = question.getVariants().size();

        if (canBeMoved(direction, index, totalCount)) {
            TestVariant variant2;
            if (direction) {
                variant2 = getVariantOfQuestionByIndex(question, index - 1);
            } else {
                variant2 = getVariantOfQuestionByIndex(question, index + 1);
            }
            try {
                swapVariants(variant, variant2);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint exception while move item", e);
            }
        }
    }

    @Transactional
    public void updateNumbers(TestVariant variant) throws EltilandManagerException {
        int index = variant.getOrderNumber();

        Criteria criteria = getCurrentSession().createCriteria(TestVariant.class);

        if (variant.getItem() != null) {
            criteria.add(Restrictions.eq("item", variant.getItem()));
        } else {
            criteria.add(Restrictions.eq("question", variant.getQuestion()));
        }
        List<TestVariant> variants = criteria.list();

        for (TestVariant v : variants) {
            int i = v.getOrderNumber();
            if (i > index) {
                v.setOrderNumber(i - 1);
                try {
                    genericManager.update(v);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint exception while move item", e);
                }
            }
        }
    }

    @Transactional
    public void deleteEntity(TestVariant variant) throws EltilandManagerException {
        genericManager.delete(variant);
    }

    @Transactional(readOnly = true)
    private TestVariant getVariantOfItemByIndex(TestCourseItem item, int index) {
        Query query = getCurrentSession().createQuery("select variant from TestVariant as variant " +
                "where variant.item.id = :id and variant.orderNumber = :index")
                .setParameter("id", item.getId())
                .setParameter("index", index);
        return (TestVariant) query.uniqueResult();
    }

    @Transactional(readOnly = true)
    private TestVariant getVariantOfQuestionByIndex(TestQuestion question, int index) {
        Query query = getCurrentSession().createQuery("select variant from TestVariant as variant " +
                "where variant.question.id = :id and variant.orderNumber = :index")
                .setParameter("id", question.getId())
                .setParameter("index", index);
        return (TestVariant) query.uniqueResult();
    }

    @Transactional
    private void swapVariants(TestVariant v1, TestVariant v2) throws ConstraintException {
        int index1 = v1.getOrderNumber();
        int index2 = v2.getOrderNumber();

        v1.setOrderNumber(index2);
        v2.setOrderNumber(index1);

        genericManager.update(v1);
        genericManager.update(v2);
    }

    private boolean canBeMoved(boolean direction, int index, int totalCount) {
        return !((direction && (index == 0)) || (!direction && (index >= (totalCount - 1))));
    }
}
