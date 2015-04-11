package com.eltiland.bl.impl.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestJumpOrder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
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
public class TestJumpOrderManagerImpl extends ManagerImpl implements TestJumpOrderManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public TestJumpOrder createJumpOrder(TestJumpOrder order) throws EltilandManagerException {
        try {
            return genericManager.saveNew(order);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation while create jump order", e);
        }
    }

    @Override
    @Transactional
    public TestJumpOrder updateJumpOrder(TestJumpOrder order) throws EltilandManagerException {
        try {
            return genericManager.update(order);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation while create jump order", e);
        }
    }

    @Override
    @Transactional
    public void deleteJumpOrder(TestJumpOrder order) throws EltilandManagerException {
        genericManager.delete(order);
    }

    @Override
    @Transactional
    public void deleteAllOrphans() throws EltilandManagerException {
        Criteria criteria = getCurrentSession().createCriteria(TestJumpOrder.class);
        criteria.add(Restrictions.isNull("jump"));
        List<TestJumpOrder> orders = criteria.list();
        for (TestJumpOrder order : orders) {
            deleteJumpOrder(order);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestJumpOrder> getSortedJumpOrders(TestJump jump) {
        Criteria criteria = getCurrentSession().createCriteria(TestJumpOrder.class);
        criteria.add(Restrictions.eq("jump", jump));
        criteria.addOrder(Order.asc("order"));
        return criteria.list();
    }
}
