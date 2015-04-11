package com.eltiland.bl.validators;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.bl.test.TestVariantManager;
import com.eltiland.exceptions.TestException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.model.course.test.TestVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validator of TestCourseItems.
 */
@Component
public class TestValidator {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private TestQuestionManager testQuestionManager;
    @Autowired
    private TestVariantManager testVariantManager;

    public void isTestValid(TestCourseItem item) throws TestException {
        genericManager.initialize(item, item.getQuestions());
        genericManager.initialize(item, item.getVariants());
        genericManager.initialize(item, item.getResults());

        if (item.getQuestions().isEmpty()) {
            throw new TestException("Тест не содержит вопросов");
        } else if (!checkisEmptySections(item)) {
            throw new TestException("В тесте присутствуют пустые секции вопросов");
        } else if (!checkWithoutResults(item)) {
            throw new TestException("В тесте присутствуют вопросы без результатов");
        } else if (!checkWithoutVariants(item)) {
            throw new TestException("В тесте присутствуют вопросы без вариантов ответа");
        } else if (!checkIsWhiteSpaces(item)) {
            throw new TestException("Баллы результатов теста не покрывают весь промежуток");
        } else if (!checkVariantsCorrect(item)) {
            throw new TestException("В тесте присутствуют баллы вариантов ответа, которые не покрываются результатами");
        } else if (!checkLimitAndRightResult(item)) {
            throw new TestException("Для теста установлен лимит количества прохождений, однако не установлен результат, считаемый как правильный");
        }
    }

    private boolean checkWithoutResults(TestCourseItem item) {
        boolean presentGlobalResults = !(item.getResults().isEmpty());
        for (TestQuestion question : testQuestionManager.getSortedTopLevelList(item)) {
            if (question.isSection()) {
                genericManager.initialize(question, question.getResults());
                if (question.getResults().isEmpty() && !presentGlobalResults) {
                    return false;
                }
            } else {
                if (!presentGlobalResults) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWithoutVariants(TestCourseItem item) {
        boolean presentGlobalVariants = !(item.getVariants().isEmpty());
        for (TestQuestion question : testQuestionManager.getSortedTopLevelList(item)) {
            if (question.isSection()) {
                genericManager.initialize(question, question.getVariants());
                if (question.getVariants().isEmpty() && !presentGlobalVariants) {
                    return false;
                }
            } else {
                if (!presentGlobalVariants) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean checkIsWhiteSpaces(TestCourseItem item) {
        boolean globalCheck = checkResultSet(item.getResults());
        if (!globalCheck) {
            return false;
        } else {
            for (TestQuestion question : item.getQuestions()) {
                if (question.isSection()) {
                    genericManager.initialize(question, question.getResults());
                    if (!(checkResultSet(question.getResults()))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkisEmptySections(TestCourseItem item) {
        for (TestQuestion question : item.getQuestions()) {
            if (question.isSection()) {
                genericManager.initialize(question, question.getChildren());
                if (question.getChildren().size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkVariantsCorrect(TestCourseItem item) {
        if (!checkVariantSet(new ArrayList<>(item.getVariants()), item.getResults())) {
            return false;
        }

        for (TestQuestion question : item.getQuestions()) {
            genericManager.initialize(question, question.getResults());
            Set<TestResult> results = question.getResults().isEmpty() ? item.getResults() : question.getResults();
            if (!checkVariantSet(testVariantManager.getVariantsForQuestion(question), results)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkVariantSet(List<TestVariant> variantSet, Set<TestResult> resultSet) {
        for (TestVariant variant : variantSet) {
            Extremum extremum = getExtremum(resultSet);
            int value = variant.getNumber();
            if (value > extremum.getMax() || value < extremum.getMin()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkResultSet(Set<TestResult> resultSet) {
        if (resultSet.isEmpty()) {
            return true;
        }

        Extremum extremum = getExtremum(resultSet);

        for (int i = extremum.getMin(); i <= extremum.getMax(); i++) {
            boolean flag = false;
            for (TestResult result : resultSet) {
                if ((i >= result.getMinValue()) && (i <= result.getMaxValue())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    private boolean checkLimitAndRightResult(TestCourseItem item) {
        if (item.getAttemptLimit() != 0) {
            genericManager.initialize(item, item.getResults());

            short hasRightResult = 0;
            for (TestResult result : item.getResults()) {
                if (result.isRightResult()) {
                    hasRightResult++;
                }
            }
            return hasRightResult == 1;
        } else {
            return true;
        }
    }

    private Extremum getExtremum(Set<TestResult> results) {
        Extremum extremum = new Extremum(-1, -1);

        for (TestResult result : results) {
            if (extremum.getMin() == -1 || (result.getMinValue() < extremum.getMin())) {
                extremum.setMin(result.getMinValue());
            }
            if (result.getMaxValue() > extremum.getMax()) {
                extremum.setMax(result.getMaxValue());
            }
        }

        return extremum;
    }


    private class Extremum {
        private int min;
        private int max;

        private Extremum(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
