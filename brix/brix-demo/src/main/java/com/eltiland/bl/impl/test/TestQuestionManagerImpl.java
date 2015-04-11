package com.eltiland.bl.impl.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.model.course.test.TestVariant;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Test Question Manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class TestQuestionManagerImpl extends ManagerImpl implements TestQuestionManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public TestQuestion updateTestQuestion(TestQuestion testQuestion) throws EltilandManagerException {
        try {
            genericManager.update(testQuestion);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating test question", e);
        }
        return testQuestion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestQuestion> getSortedTopLevelList(TestCourseItem item) {
        Query query = getCurrentSession().createQuery("select question from TestQuestion as question " +
                "where question.item.id = :id and " +
                "(question.section = true or question.parentItem = null) " +
                "order by question.number asc")
                .setParameter("id", item.getId());
        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestQuestion> getSortedList(TestQuestion parent) {
        Query query = getCurrentSession().createQuery("select question from TestQuestion as question " +
                "where question.parentItem.id = :id " +
                "order by question.number asc")
                .setParameter("id", parent.getId());
        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTopLevelItemCount(TestCourseItem item) {
        Query query = getCurrentSession().createQuery("select count(question) from TestQuestion as question " +
                "where question.item.id = :id and " +
                "(question.section = true or question.parentItem = null)")
                .setParameter("id", item.getId());
        Long count = (Long) query.uniqueResult();
        return count.intValue();
    }

    @Override
    @Transactional
    public void deleteTestQuestion(TestQuestion item) throws EltilandManagerException {
        int currentPosition = item.getNumber();
        TestQuestion parent = item.getParentItem();
        int count = getChildCount(item);

        genericManager.initialize(item, item.getItem());
        if ((currentPosition + 1) < count) {
            for (int i = currentPosition + 1; i < count; i++) {
                moveTestQuestionUp(getQuestionByPosition(i, item.getItem(), parent));
            }
        }

        genericManager.initialize(item, item.getChildren());
        genericManager.initialize(item, item.getVariants());
        genericManager.initialize(item, item.getResults());
        for (TestQuestion childQuestion : item.getChildren()) {
            genericManager.delete(childQuestion);
        }
        for (TestVariant childVariant : item.getVariants()) {
            genericManager.delete(childVariant);
        }
        for (TestResult childResult : item.getResults()) {
            genericManager.delete(childResult);
        }

        genericManager.delete(item);
    }

    @Override
    @Transactional
    public void moveTestQuestionUp(TestQuestion item) throws EltilandManagerException {
        if (item != null) {
            int index = item.getNumber();
            if (index != 0) {
                TestQuestion tItem = getQuestionByPosition(index - 1, item.getItem(), item.getParentItem());
                swapQuestions(item, tItem);
            }
        }
    }

    @Override
    @Transactional
    public void moveTestQuestionDown(TestQuestion item) throws EltilandManagerException {
        if (item != null) {
            int index = item.getNumber();
            int maxIndex = getChildCount(item) - 1;

            if (index != maxIndex) {
                TestQuestion tItem = getQuestionByPosition(index + 1, item.getItem(), item.getParentItem());
                swapQuestions(item, tItem);
            }
        }
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    @Transactional(readOnly = true)
    public TestQuestion getQuestionByPosition(int position, TestCourseItem item, TestQuestion parent) {
        String queryString = "select question from TestQuestion as question " +
                "where question.number = :position and question.item = :item";
        if (parent == null) {
            queryString += " and question.parentItem = null";
        } else {
            queryString += " and question.parentItem = :parent";
        }

        Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("position", position);
        query.setParameter("item", item);

        if (parent != null) {
            query.setParameter("parent", parent);
        }

        return (TestQuestion) query.uniqueResult();
    }

    @Transactional
    private void swapQuestions(TestQuestion question1, TestQuestion question2) throws EltilandManagerException {
        int pos1 = question1.getNumber();
        int pos2 = question2.getNumber();

        question1.setNumber(pos2);
        question2.setNumber(pos1);

        updateTestQuestion(question1);
        updateTestQuestion(question2);
    }

    private int getChildCount(TestQuestion question) {
        genericManager.initialize(question, question.getParentItem());
        TestQuestion parent = question.getParentItem();
        int count;

        if (parent != null) {
            genericManager.initialize(parent, parent.getChildren());
            count = parent.getChildren().size();
        } else {
            count = getTopLevelItemCount(question.getTestItem());
        }

        return count;
    }
}
