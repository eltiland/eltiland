package com.eltiland.bl.impl.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestResultManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course test result manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class TestResultManagerImpl extends ManagerImpl implements TestResultManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private TestJumpManager testJumpManager;

    @Override
    @Transactional(readOnly = true)
    public TestResult getResult(TestCourseItem testItem, int value) {
        Query q = getCurrentSession()
                .createQuery("select result from TestResult as result"
                        + " where result.item.id = :id and"
                        + " result.maxValue >= :value " +
                        " and result.minValue <= :value").
                        setParameter("value", value).
                        setParameter("id", testItem.getId());
        return ((TestResult) q.uniqueResult());
    }

    @Override
    @Transactional(readOnly = true)
    public TestResult getResult(TestQuestion question, int value) {
        Query q = getCurrentSession()
                .createQuery("select result from TestResult as result"
                        + " where result.question.id = :id and"
                        + " result.maxValue >= :value " +
                        " and result.minValue <= :value").
                        setParameter("value", value).
                        setParameter("id", question.getId());
        return ((TestResult) q.uniqueResult());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResult> getResultsForItem(TestCourseItem item) {
        Query query = getCurrentSession().createQuery("select result from TestResult as result " +
                "where result.item.id = :id")
                .setParameter("id", item.getId());
        return query.list();
    }

    @Override
    @Transactional
    public void deleteTestResult(TestResult testResult) throws EltilandManagerException {
        genericManager.initialize(testResult, testResult.getJumps());
        for (TestJump jump : testResult.getJumps()) {
            testJumpManager.deleteJump(jump);
        }
        genericManager.delete(testResult);
    }

    @Override
    @Transactional
    public void updateRightFlag(TestCourseItem item, TestResult testResult) throws EltilandManagerException {
        genericManager.initialize(item, item.getResults());

        for (TestResult result : item.getResults()) {
            if ((testResult.getId() == null) || (!(testResult.getId().equals(result.getId())))) {
                result.setRightResult(false);
                try {
                    genericManager.update(result);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint exception", e);
                }
            }
        }
    }
}
