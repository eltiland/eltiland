package com.eltiland.bl.impl.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestJumpOrder;
import com.eltiland.model.course.test.TestResult;
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
public class TestJumpManagerImpl extends ManagerImpl implements TestJumpManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private TestJumpOrderManager testJumpOrderManager;

    @Override
    @Transactional
    public TestJump createJump(TestJump jump) throws EltilandManagerException {
        try {
            return genericManager.saveNew(jump);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation while creating jump", e);
        }
    }

    @Override
    @Transactional
    public TestJump updateJump(TestJump jump) throws EltilandManagerException {
        try {
            return genericManager.update(jump);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation while updating jump", e);
        }
    }

    @Override
    @Transactional
    public void deleteJump(TestJump jump) throws EltilandManagerException {
        genericManager.initialize(jump, jump.getPrevs());
        for (TestJumpOrder order : jump.getPrevs()) {
            testJumpOrderManager.deleteJumpOrder(order);
        }

        int maxIndex = jump.getResult().getJumps().size();
        for (int i = (jump.getJumpOrder() + 1); i < maxIndex; i++) {
            moveUp(getJumpByPosition(i, jump.getResult()));
        }

        genericManager.delete(jump);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestJump> getSortedJumps(TestResult result) {
        Criteria criteria = getCurrentSession().createCriteria(TestJump.class);
        criteria.add(Restrictions.eq("result", result));
        criteria.addOrder(Order.asc("jumpOrder"));
        return criteria.list();
    }

    @Override
    @Transactional
    public void moveUp(TestJump jump) throws EltilandManagerException {
        if (jump != null) {
            int index = jump.getJumpOrder();
            if (index != 0) {
                TestJump tItem = getJumpByPosition(index - 1, jump.getResult());
                swapQuestions(jump, tItem);
            }
        }
    }

    @Override
    @Transactional
    public void moveDown(TestJump jump) throws EltilandManagerException {
        if (jump != null) {
            int index = jump.getJumpOrder();
            int maxIndex = jump.getResult().getJumps().size();

            if (index != maxIndex) {
                TestJump tItem = getJumpByPosition(index + 1, jump.getResult());
                swapQuestions(jump, tItem);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TestJump getJumpByPosition(int position, TestResult result) {
        Criteria criteria = getCurrentSession().createCriteria(TestJump.class);
        criteria.add(Restrictions.eq("result", result));
        criteria.add(Restrictions.eq("jumpOrder", position));
        return (TestJump) criteria.uniqueResult();
    }

    @Transactional
    private void swapQuestions(TestJump jump1, TestJump jump2) throws EltilandManagerException {
        int pos1 = jump1.getJumpOrder();
        int pos2 = jump2.getJumpOrder();

        jump1.setJumpOrder(pos2);
        jump2.setJumpOrder(pos1);

        try {
            genericManager.update(jump1);
            genericManager.update(jump2);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint exception during movement of jumps", e);
        }
    }
}
