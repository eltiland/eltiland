package com.eltiland.ui.course.components.editPanels.elements.test.list;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.model.course.test.TestVariant;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AbstractTestPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.QuestionPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.result.ResultPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.VariantPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.EntityInfoPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.QuestionInfoPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Panel for Question/Section list for tests.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class QuestionPanel extends EntityPanel<TestQuestion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionPanel.class);

    @SpringBean
    private TestQuestionManager testQuestionManager;
    @SpringBean
    private GenericManager genericManager;

    protected Dialog<AbstractTestPropertyPanel> variantDialog =
            new Dialog<AbstractTestPropertyPanel>("variantDialog", DIALOG_WIDTH) {
                @Override
                public AbstractTestPropertyPanel createDialogPanel(String id) {
                    return new VariantPropertyPanel(id, QuestionPanel.this.getModel()) {
                        @Override
                        protected void OnCreate(AjaxRequestTarget target) {
                            TestVariant variant = new TestVariant();
                            variant.setQuestion(getParentModel().getObject());
                            variant = fillEntity(variant);
                            variant.setTestItem(null);
                            createEntity(variant);
                            variantDialog.close(target);
                            updateList(target);
                        }

                        @Override
                        protected void OnSave(AjaxRequestTarget target) {
                            TestVariant variant = (TestVariant) variantDialog.getDialogPanel().getModelObject();
                            variant = fillEntity(variant);
                            variant.setTestItem(null);
                            updateEntity(variant);
                            variantDialog.close(target);
                            updateList(target);
                        }
                    };
                }
            };

    protected Dialog<AbstractTestPropertyPanel> resultDialog =
            new Dialog<AbstractTestPropertyPanel>("resultDialog", DIALOG_WIDTH) {
                @Override
                public AbstractTestPropertyPanel createDialogPanel(String id) {
                    return new ResultPropertyPanel(id, QuestionPanel.this.getModel()) {
                        @Override
                        protected void OnCreate(AjaxRequestTarget target) {
                            TestResult result = new TestResult();
                            result.setQuestion(getParentModel().getObject());
                            result = fillEntity(result);
                            result.setTestItem(null);
                            createEntity(result);
                            resultDialog.close(target);
                            updateList(target);
                        }

                        @Override
                        protected void OnSave(AjaxRequestTarget target) {
                            TestResult result = (TestResult) resultDialog.getDialogPanel().getModelObject();
                            result = fillEntity(result);
                            result.setTestItem(null);
                            updateEntity(result);
                            resultDialog.close(target);
                            updateList(target);
                        }
                    };
                }
            };


    /**
     * Panel constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test course item model.
     */
    public QuestionPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);
        add(variantDialog);
        add(resultDialog);
    }

    @Override
    protected AbstractTestPropertyPanel getPropertyPanel(String id) {
        return new QuestionPropertyPanel(id, QuestionPanel.this.getModel()) {
            @Override
            protected void OnCreate(AjaxRequestTarget target) {
                int topLevelNumber = testQuestionManager.getTopLevelItemCount(QuestionPanel.this.getModelObject());

                if (getIsCheckedSection()) {
                    TestQuestion emptySection = new TestQuestion();
                    emptySection.setNumber(topLevelNumber);
                    emptySection.setSection(true);
                    emptySection.setTestItem(QuestionPanel.this.getModelObject());
                    try {
                        genericManager.saveNew(emptySection);
                    } catch (ConstraintException e) {
                        LOGGER.error("Error while creating entity");
                        throw new WicketRuntimeException("Error while creating entity", e);
                    }
                    setParentModel(new GenericDBModel<>(TestQuestion.class, emptySection));
                }

                TestQuestion question = new TestQuestion();

                if (getParentModel().getObject() == null) {
                    question.setNumber(topLevelNumber);
                } else {
                    genericManager.initialize(getParentModel().getObject(), getParentModel().getObject().getChildren());
                    question.setParentItem(getParentModel().getObject());
                    question.setNumber(getParentModel().getObject().getChildren().size());
                }

                createEntity(fillEntity(question));

                setParentModel(new GenericDBModel<>(TestQuestion.class));
                propertyDialog.close(target);
                updateList(target);
            }

            @Override
            protected void OnSave(AjaxRequestTarget target) {
                TestQuestion question = (TestQuestion) propertyDialog.getDialogPanel().getModelObject();

                updateEntity(fillEntity(question));
                propertyDialog.close(target);
                updateList(target);
            }
        };
    }

    @Override
    protected AbstractTestPropertyPanel getSecondPropertyPanel(String id) {
        return new QuestionPropertyPanel(id, QuestionPanel.this.getModel()) {
            @Override
            protected void OnCreate(AjaxRequestTarget target) {
                TestQuestion question = new TestQuestion();
                question.setSection(true);
                question.setNumber(testQuestionManager.getTopLevelItemCount(QuestionPanel.this.getModelObject()));

                createEntity(fillEntity(question));

                propertySecondDialog.close(target);
                updateList(target);
            }

            @Override
            protected void OnSave(AjaxRequestTarget target) {
                TestQuestion question = (TestQuestion) propertySecondDialog.getDialogPanel().getModelObject();

                updateEntity(fillEntity(question));
                propertySecondDialog.close(target);
                updateList(target);
            }

            @Override
            protected String getCreateKey() {
                return "addSectionHeader";
            }

            @Override
            protected String getSaveHeader() {
                return "saveSectionHeader";
            }

            @Override
            protected String getTextHeader() {
                return "section";
            }
        };
    }

    @Override
    protected IModel<List<TestQuestion>> getEntityListModel() {
        return new GenericDBListModel<>(
                TestQuestion.class, testQuestionManager.getSortedTopLevelList(getModelObject()));
    }

    @Override
    protected EntityInfoPanel<TestQuestion> getEntityInfoPanel(String id, IModel<TestQuestion> model) {
        return new QuestionInfoPanel(id, model) {
            @Override
            protected void updateList(AjaxRequestTarget target) {
                QuestionPanel.this.updateList(target);
            }

            @Override
            protected void onEdit(AjaxRequestTarget target, IModel<TestQuestion> model) {
                if (model.getObject().isSection()) {
                    propertySecondDialog.getDialogPanel().initEditMode(model.getObject());
                    propertySecondDialog.show(target);
                } else {
                    propertyDialog.getDialogPanel().initEditMode(model.getObject());
                    propertyDialog.show(target);
                }
            }

            @Override
            protected void onEditVariant(AjaxRequestTarget target, IModel<TestVariant> model) {
                variantDialog.getDialogPanel().initEditMode(model.getObject());
                variantDialog.show(target);
            }

            @Override
            protected void onEditResult(AjaxRequestTarget target, IModel<TestResult> model) {
                resultDialog.getDialogPanel().setParentModel(getModel());
                resultDialog.getDialogPanel().initEditMode(model.getObject());
                resultDialog.show(target);
            }

            @Override
            protected void onAddQuestion(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
                ((QuestionPropertyPanel) propertyDialog.getDialogPanel()).setCheckBoxShown(false);
                propertyDialog.getDialogPanel().setParentModel(parentModel);
                propertyDialog.show(target);
            }

            @Override
            protected void onAddVariant(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
                variantDialog.getDialogPanel().setParentModel(parentModel);
                variantDialog.getDialogPanel().initCreateMode();
                variantDialog.show(target);
            }

            @Override
            protected void onAddResult(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
                resultDialog.getDialogPanel().setParentModel(parentModel);
                resultDialog.getDialogPanel().initCreateMode();
                resultDialog.show(target);
            }
        };
    }

    @Override
    protected boolean hasSecondAction() {
        return true;
    }
}
