package com.eltiland.ui.course.content;

import com.eltiland.bl.CourseListenerManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.*;
import com.eltiland.bl.validators.TestValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.TestException;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.test.*;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * Panel for output simple test.
 *
 * @author Aleksey Plotnikov.
 */
public class TestContentPanel extends CourseContentPanel<TestCourseItem> {

    @SpringBean
    private TestValidator testValidator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestQuestionManager testQuestionManager;
    @SpringBean
    private TestVariantManager testVariantManager;
    @SpringBean
    private TestResultManager testResultManager;
    @SpringBean
    private TestJumpOrderManager testJumpOrderManager;
    @SpringBean
    private TestJumpManager testJumpManager;
    @SpringBean
    private TestAttemptManager testAttemptManager;
    @SpringBean
    private CourseListenerManager courseListenerManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestContentPanel.class);

    private int currentQuestion = 0;
    private int currentSubQuestion = 0;
    private int sum = 0;
    private int subSum = 0;
    private boolean finished = false;
    private boolean isTestValid = true;
    private boolean isSubResult = false;
    private boolean isLimitReached = false;
    private boolean isCompleted = false;
    private boolean access = false;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    /**
     * Top level question model.
     */
    private IModel<TestQuestion> questionModel = new LoadableDetachableModel<TestQuestion>() {
        @Override
        protected TestQuestion load() {
            return testQuestionManager.getQuestionByPosition(
                    currentQuestion, TestContentPanel.this.getModelObject(), null);
        }
    };

    /**
     * Second level question model.
     */
    private IModel<TestQuestion> subQuestionModel = new LoadableDetachableModel<TestQuestion>() {
        @Override
        protected TestQuestion load() {
            return testQuestionManager.getQuestionByPosition(
                    currentSubQuestion, TestContentPanel.this.getModelObject(), questionModel.getObject());
        }
    };

    private Stack<TestQuestion> questionStack = new Stack<>();

    /**
     * Variant selection model.
     */
    private IModel<List<? extends TestVariant>> variantModel =
            new LoadableDetachableModel<List<? extends TestVariant>>() {
                @Override
                protected List<? extends TestVariant> load() {
                    if (questionModel.getObject().isSection()) {
                        genericManager.initialize(questionModel.getObject(),
                                questionModel.getObject().getVariants());
                        if (!(questionModel.getObject().getVariants().isEmpty())) {
                            isSubResult = true;
                            return testVariantManager.getVariantsForQuestion(questionModel.getObject());
                        }
                    }
                    TestCourseItem item = TestContentPanel.this.getModelObject();
                    genericManager.initialize(item, item.getVariants());
                    isSubResult = false;
                    return testVariantManager.getVariantsForItem(item);
                }
            };

    private IModel<List<TestResult>> results = new GenericDBListModel<>(TestResult.class);

    /**
     * Container for output section name.
     */
    private WebMarkupContainer sectionContainer = new WebMarkupContainer("sectionContainer") {
        @Override
        public boolean isVisible() {
            return !finished && isTestValid && !(questionModel.getObject().getText() == null) &&
                    !(questionModel.getObject().getText().isEmpty()) && !isLimitReached && !isCompleted;
        }
    };

    /**
     * Container for output count of the attempts for test.
     */
    private WebMarkupContainer limitContainer = new WebMarkupContainer("limitContainer") {
        @Override
        public boolean isVisible() {
            TestCourseItem item = TestContentPanel.this.getModelObject();
            genericManager.initialize(item, item.getCourseFull());
            return (item.getCourseFull() != null) && (item.getAttemptLimit() > 0) && !isLimitReached && !isCompleted;
        }
    };

    /**
     * Top level Section/Question text.
     */
    private Label sectionName = new Label("sectionName", new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            if (questionModel.getObject() != null) {
                return questionModel.getObject().getText();
            } else {
                return null;
            }
        }
    }) {
        @Override
        protected void onBeforeRender() {
            super.onBeforeRender();
            if (questionModel.getObject() != null) {
                add(new AttributeModifier("class", questionModel.getObject().isSection() ? "section" : "question"));
            }
        }
    };

    /**
     * Second level question text.
     */
    private Label questionName = new Label("questionName", new LoadableDetachableModel<Object>() {
        @Override
        protected Object load() {
            if (subQuestionModel.getObject() != null) {
                return subQuestionModel.getObject().getText();
            } else {
                return null;
            }
        }
    }) {
        @Override
        public boolean isVisible() {
            return questionModel.getObject() != null && questionModel.getObject().isSection();
        }
    };

    /**
     * Container for output result.
     */
    private WebMarkupContainer resultContainer = new WebMarkupContainer("resultContainer") {
        @Override
        public boolean isVisible() {
            return finished;
        }
    };

    private WebMarkupContainer limitReachedContainer = new WebMarkupContainer("limitReachedContainer") {
        @Override
        public boolean isVisible() {
            return isLimitReached;
        }
    };

    private WebMarkupContainer completedContainer = new WebMarkupContainer("completedContainer") {
        @Override
        public boolean isVisible() {
            return isCompleted;
        }
    };

    private WebMarkupContainer content;

    /**
     * Panel constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test model.
     */
    public TestContentPanel(String id, final IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);

        isTestValid = isAttachShown();

        content.add(new WebMarkupContainer("processMessageContainer") {
            @Override
            public boolean isVisible() {
                return !isTestValid;
            }
        });
        content.add(limitContainer);
        content.add(limitReachedContainer);
        content.add(completedContainer);
        content.add(sectionContainer.setOutputMarkupId(true));

        Date start = getModelObject().getAccessStartDate();
        Date end = getModelObject().getAccessEndDate();
        access = start == null || end == null ||
                start.before(DateUtils.getCurrentDate()) && end.after(DateUtils.getCurrentDate());

        if (getModelObject().getAttemptLimit() > 0 && access) {
            if (!(testAttemptManager.hasAttemptRecord(getModelObject()))) {
                UserTestAttempt attempt = new UserTestAttempt();
                attempt.setUser(currentUserModel.getObject());
                attempt.setTest(getModelObject());
                attempt.setAttemptCount(0);
                attempt.setAttemptLimit(getModelObject().getAttemptLimit());

                try {
                    genericManager.saveNew(attempt);
                } catch (ConstraintException e) {
                    LOGGER.error("Cannot create attempt entity", e);
                    throw new WicketRuntimeException("Cannot create attempt entity", e);
                }
            }


            UserTestAttempt attempt = testAttemptManager.getAttempt(getModelObject());
            int current = attempt.getAttemptCount();
            int limit = attempt.getAttemptLimit();

            if (current <= limit) {
                try {
                    current++;
                    isLimitReached = (current > limit);
                    if (!isLimitReached) {
                        attempt.setAttemptCount(current);
                        genericManager.update(attempt);
                    }
                } catch (ConstraintException e) {
                    LOGGER.error("Cannot increase attempt", e);
                    throw new WicketRuntimeException("Cannot increase attempt", e);
                }
            }
            isCompleted = attempt.isCompleted();
            limitContainer.add(new Label("limitValue",
                    String.format(getString("limitValueLabel"), current, limit)));
        }


        genericManager.initialize(testCourseItemIModel.getObject(), testCourseItemIModel.getObject().getQuestions());
        final int qCount = testQuestionManager.getTopLevelItemCount(testCourseItemIModel.getObject());

        sectionContainer.add(sectionName.setOutputMarkupId(true));
        sectionContainer.add(questionName.setOutputMarkupPlaceholderTag(true));

        content.add(resultContainer.setOutputMarkupPlaceholderTag(true));

        resultContainer.add(new ListView<TestResult>("resultList", results) {
            @Override
            protected void populateItem(ListItem<TestResult> item) {
                Label label = new Label("result", item.getModel().getObject().getResult());
                genericManager.initialize(item.getModelObject(), item.getModelObject().getQuestion());
                label.add(new AttributeModifier("class",
                        (item.getModelObject().getQuestion() == null) ? "resultGlobalText" : "resultText"));
                item.add(label);
            }
        });

        sectionContainer.add(new ListView<TestVariant>("variantsList", variantModel) {
            @Override
            protected void populateItem(final ListItem<TestVariant> item) {
                EltiAjaxLink variantButton = new EltiAjaxLink("variant") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        TestQuestion jump = null;
                        boolean finResult = false;
                        int currentValue = item.getModelObject().getNumber();
                        sum += currentValue;
                        subSum += currentValue;

                        boolean isSection = questionModel.getObject().isSection();
                        if (isSection) {
                            genericManager.initialize(questionModel.getObject(),
                                    questionModel.getObject().getChildren());
                            int cCount = questionModel.getObject().getChildren().size();

                            if (currentSubQuestion + 1 == cCount) {
                                currentSubQuestion = 0;
                                isSection = false;
                                TestResult result = testResultManager.getResult(questionModel.getObject(), subSum);
                                if (result != null) {

                                    // check for jumps
                                    genericManager.initialize(result, result.getJumps());
                                    if (!(result.getJumps().isEmpty())) {

                                        // check for conditional jump
                                        for (TestJump j : testJumpManager.getSortedJumps(result)) {
                                            genericManager.initialize(j, j.getPrevs());
                                            if (!(j.getPrevs().isEmpty())) {
                                                // check jump conditions
                                                List<TestJumpOrder> orders =
                                                        testJumpOrderManager.getSortedJumpOrders(j);
                                                Stack<TestQuestion> t_stack = new Stack<>();
                                                boolean correct_jump = true;

                                                // check for jump to current question
                                                TestQuestion testPrev = questionStack.peek();
                                                if (testPrev != null &&
                                                        testPrev.getId().equals(questionModel.getObject().getId())) {
                                                    testPrev = questionStack.pop();
                                                    t_stack.push(testPrev);
                                                }

                                                for (TestJumpOrder order : orders) {
                                                    genericManager.initialize(order, order.getQuestion());
                                                    TestQuestion question = questionStack.pop();
                                                    t_stack.push(question);
                                                    if (!(question.getId().equals(order.getQuestion().getId()))) {
                                                        correct_jump = false;
                                                        break;
                                                    }
                                                }

                                                // repair stack
                                                while (!(t_stack.empty())) {
                                                    questionStack.push(t_stack.pop());
                                                }

                                                // all conditions passed.
                                                if (correct_jump) {
                                                    genericManager.initialize(j, j.getDest());
                                                    jump = j.getDest();
                                                }
                                            }

                                            if (jump != null) {
                                                break;
                                            }
                                        }

                                        // check for jump without condition
                                        if (jump == null) {
                                            for (TestJump j : result.getJumps()) {
                                                genericManager.initialize(j, j.getPrevs());
                                                if (j.getPrevs().isEmpty()) {
                                                    genericManager.initialize(j, j.getDest());
                                                    jump = j.getDest();
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    String res = result.getResult();
                                    if ((!(results.getObject().contains(result))) && (res != null) && (!res.isEmpty())) {
                                        results.getObject().add(result);
                                    }
                                    finResult = result.isJumpFinish();
                                }
                                subSum = 0;
                            } else {
                                currentSubQuestion++;
                            }
                        }
                        if (!isSection) {
                            if ((((currentQuestion + 1) == qCount) && (jump == null)) || (finResult)) {
                                finished = true;
                                TestResult result =
                                        testResultManager.getResult(TestContentPanel.this.getModelObject(), sum);

                                if (result != null) {
                                    //set completed flag for attempt entity
                                    if (result.isRightResult() &&
                                            (TestContentPanel.this.getModelObject().getAttemptLimit() > 0)) {
                                        try {
                                            UserTestAttempt attempt =
                                                    testAttemptManager.getAttempt(TestContentPanel.this.getModelObject());
                                            attempt.setCompleted(true);
                                            genericManager.update(attempt);

                                            genericManager.initialize(TestContentPanel.this.getModelObject(),
                                                    TestContentPanel.this.getModelObject().getCourse());
                                            CourseListener listener = courseListenerManager.getListener(
                                                    TestContentPanel.this.getModelObject().getCourse(),
                                                    currentUserModel.getObject());
                                            if (listener != null) {
                                                listener.setCompleted(true);
                                                genericManager.update(listener);
                                            }

                                        } catch (ConstraintException e) {
                                            LOGGER.error("Cannot update attempt", e);
                                            throw new WicketRuntimeException("Cannot update attempt", e);
                                        }
                                    }
                                    results.getObject().add(result);
                                }

                                target.add(sectionContainer);
                                target.add(resultContainer);
                                return;
                            } else {
                                if (jump == null) {
                                    currentQuestion++;
                                } else {
                                    currentQuestion = jump.getNumber();
                                }
                            }
                        }

                        if (questionStack.empty()) {
                            questionStack.push(questionModel.getObject());
                        } else {
                            TestQuestion prevQuestion = questionStack.peek();
                            if (prevQuestion.getNumber() != questionModel.getObject().getNumber()) {
                                questionStack.push(questionModel.getObject());
                            }
                        }

                        subQuestionModel.detach();
                        questionModel.detach();
                        variantModel.detach();
                        target.add(questionName);
                        target.add(sectionName);
                        target.add(sectionContainer);
                    }
                };
                variantButton.add(new Label("variantText", item.getModelObject().getValue()));
                item.add(variantButton);
            }
        });
    }

    @Override
    protected WebMarkupContainer getContent() {
        content = new WebMarkupContainer("content");
        return content;
    }

    @Override
    protected boolean isAttachShown() {
        try {
            testValidator.isTestValid(getModelObject());
        } catch (TestException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void increaseAttempt() {
        super.increaseAttempt();
        if (getModelObject().getAttemptLimit() > 0 && access) {
            UserTestAttempt attempt = testAttemptManager.getAttempt(getModelObject());
            int current = attempt.getAttemptCount();
            int limit = attempt.getAttemptLimit();

            if (current <= limit) {
                try {
                    current++;
                    isLimitReached = (current > limit);
                    if (!isLimitReached) {
                        attempt.setAttemptCount(current);
                        genericManager.update(attempt);
                    }
                } catch (ConstraintException e) {
                    LOGGER.error("Cannot increase attempt", e);
                    throw new WicketRuntimeException("Cannot increase attempt", e);
                }
            }
            isCompleted = attempt.isCompleted();
            limitContainer.add(new Label("limitValue",
                    String.format(getString("limitValueLabel"), current, limit)));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE);
    }
}
