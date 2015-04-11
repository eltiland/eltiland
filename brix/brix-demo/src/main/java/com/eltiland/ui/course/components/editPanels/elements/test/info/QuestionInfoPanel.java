package com.eltiland.ui.course.components.editPanels.elements.test.info;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.bl.test.TestVariantManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.model.course.test.TestVariant;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Test Question info panel.
 *
 * @author ALeksey PLotnikov.
 */
public abstract class QuestionInfoPanel extends EntityInfoPanel<TestQuestion> {

    @SpringBean
    private TestQuestionManager testQuestionManager;
    @SpringBean
    private TestVariantManager testVariantManager;
    @SpringBean
    private GenericManager genericManager;

    protected static final Logger LOGGER = LoggerFactory.getLogger(QuestionInfoPanel.class);

    /**
     * Panel constructor.
     *
     * @param id                 markup id.
     * @param testQuestionIModel entity model.
     */
    public QuestionInfoPanel(String id, final IModel<TestQuestion> testQuestionIModel) {
        super(id, testQuestionIModel);

        if (isSection()) {
            infoContainer.add(new AttributeAppender("class", new Model<>("sectionPanel"), " "));
        }
    }

    @Override
    protected boolean isSection() {
        return getModelObject().isSection();
    }

    @Override
    protected boolean isLabelBold() {
        return true;
    }

    @Override
    protected boolean showNumbers() {
        return getModelObject().isSection();
    }

    @Override
    protected boolean canBeDeleted() {
        genericManager.initialize(getModelObject(), getModelObject().getJumps());
        return getModelObject().getJumps().isEmpty();
    }

    @Override
    protected void onDelete(TestQuestion entity) throws EltilandManagerException {
        testQuestionManager.deleteTestQuestion(entity);
    }

    @Override
    protected Component getAdditionInfoComponent() {
        return new ActionPanel("actionPanel", getModel()) {
            @Override
            public boolean isVisible() {
                return QuestionInfoPanel.this.getModelObject().isSection();
            }
        };
    }

    @Override
    protected Component getChildComponent() {
        return new ListPanel("listPanel", getModel());
    }

    protected void onAddQuestion(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
    }

    protected void onAddVariant(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
    }

    protected void onAddResult(AjaxRequestTarget target, IModel<TestQuestion> parentModel) {
    }

    private class ActionPanel extends BaseEltilandPanel<TestQuestion> {

        private ActionPanel(String id, final IModel<TestQuestion> testQuestionIModel) {
            super(id, testQuestionIModel);

            EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onEdit(target, testQuestionIModel);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }
            };

            EltiAjaxLink cancelButton = new EltiAjaxLink("cancelButton") {
                {
                    add(new ConfirmationDialogBehavior());
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        onDelete(testQuestionIModel.getObject());
                        updateList(target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Error while removing entity");
                        throw new WicketRuntimeException("Error while removing entity", e);
                    }
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }

                @Override
                public boolean isVisible() {
                    return canBeDeleted();
                }
            };

            EltiAjaxLink addQuestionButton = new EltiAjaxLink("addQuestionButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onAddQuestion(target, ActionPanel.this.getModel());
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }
            };

            EltiAjaxLink addVariantButton = new EltiAjaxLink("addVariantButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onAddVariant(target, ActionPanel.this.getModel());
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }
            };

            EltiAjaxLink addResultButton = new EltiAjaxLink("addResultButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onAddResult(target, ActionPanel.this.getModel());
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }
            };

            EltiAjaxLink upButton = new EltiAjaxLink("upButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        testQuestionManager.moveTestQuestionUp(testQuestionIModel.getObject());
                        updateList(target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Error while moving entity");
                        throw new WicketRuntimeException("Error while moving entity", e);
                    }
                }

                @Override
                public boolean isVisible() {
                    return testQuestionIModel.getObject().getNumber() != 0;
                }
            };

            EltiAjaxLink downButton = new EltiAjaxLink("downButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        testQuestionManager.moveTestQuestionDown(testQuestionIModel.getObject());
                        updateList(target);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Error while moving entity");
                        throw new WicketRuntimeException("Error while moving entity", e);
                    }
                }

                @Override
                public boolean isVisible() {
                    genericManager.initialize(testQuestionIModel.getObject(), testQuestionIModel.getObject().getItem());
                    TestCourseItem item = testQuestionIModel.getObject().getItem();
                    int number = testQuestionIModel.getObject().getNumber();

                    return (number < (testQuestionManager.getTopLevelItemCount(item) - 1));
                }
            };

            add(addQuestionButton);
            add(addVariantButton);
            add(addResultButton);
            add(editButton);
            add(cancelButton);
            add(upButton);
            add(downButton);

            addQuestionButton.add(new AttributeModifier("title", new ResourceModel("addQuestionTooltip")));
            addQuestionButton.add(new TooltipBehavior());
            addVariantButton.add(new AttributeModifier("title", new ResourceModel("addVariantTooltip")));
            addVariantButton.add(new TooltipBehavior());
            addResultButton.add(new AttributeModifier("title", new ResourceModel("addResultTooltip")));
            addResultButton.add(new TooltipBehavior());
            editButton.add(new AttributeModifier("title", new ResourceModel("editLabel")));
            editButton.add(new TooltipBehavior());
            cancelButton.add(new AttributeModifier("title", new ResourceModel("removeLabel")));
            cancelButton.add(new TooltipBehavior());
            upButton.add(new AttributeModifier("title", new ResourceModel("upTooltip")));
            upButton.add(new TooltipBehavior());
            downButton.add(new AttributeModifier("title", new ResourceModel("downTooltip")));
            downButton.add(new TooltipBehavior());
        }
    }

    private class ListPanel extends BaseEltilandPanel<TestQuestion> {

        protected ListPanel(String id, IModel<TestQuestion> testQuestionIModel) {
            super(id, testQuestionIModel);

            add(new WebMarkupContainer("questionHeader") {
                @Override
                public boolean isVisible() {
                    TestQuestion question = QuestionInfoPanel.this.getModelObject();
                    genericManager.initialize(question, question.getChildren());
                    return !(question.getChildren().isEmpty());
                }
            });

            add(new WebMarkupContainer("variantHeader") {
                @Override
                public boolean isVisible() {
                    TestQuestion question = QuestionInfoPanel.this.getModelObject();
                    genericManager.initialize(question, question.getVariants());
                    return !(question.getVariants().isEmpty());
                }
            });

            add(new WebMarkupContainer("resultHeader") {
                @Override
                public boolean isVisible() {
                    TestQuestion question = QuestionInfoPanel.this.getModelObject();
                    genericManager.initialize(question, question.getResults());
                    return !(question.getResults().isEmpty());
                }
            });


            add(new ListView<TestQuestion>("childList",
                    new GenericDBListModel<>(TestQuestion.class,
                            testQuestionManager.getSortedList(QuestionInfoPanel.this.getModelObject()))) {
                @Override
                protected void populateItem(ListItem<TestQuestion> item) {
                    item.add(new QuestionInfoPanel("childInfoPanel", item.getModel()) {
                        @Override
                        protected void updateList(AjaxRequestTarget target) {
                            QuestionInfoPanel.this.updateList(target);
                        }

                        @Override
                        protected void onEdit(AjaxRequestTarget target, IModel<TestQuestion> model) {
                            QuestionInfoPanel.this.onEdit(target, model);
                        }
                    });
                }
            });

            genericManager.initialize(QuestionInfoPanel.this.getModelObject(),
                    QuestionInfoPanel.this.getModelObject().getVariants());

            add(new ListView<TestVariant>("variantList",
                    new GenericDBListModel<>(TestVariant.class,
                            new ArrayList<>(testVariantManager.getVariantsForQuestion(
                                    QuestionInfoPanel.this.getModelObject())))) {
                @Override
                protected void populateItem(final ListItem<TestVariant> item) {
                    item.add(new VariantInfoPanel("variantInfoPanel", item.getModel()) {
                        @Override
                        protected void updateList(AjaxRequestTarget target) {
                            QuestionInfoPanel.this.updateList(target);
                        }

                        @Override
                        protected void onEdit(AjaxRequestTarget target, IModel<TestVariant> model) {
                            onEditVariant(target, model);
                        }

                        @Override
                        protected boolean canBeMovedUp() {
                            TestVariant variant = genericManager.getObject(TestVariant.class,
                                    item.getModelObject().getId());
                            int number = variant.getOrderNumber();
                            return number > 0;
                        }

                        @Override
                        protected boolean canBeMovedDown() {
                            TestVariant variant = genericManager.getObject(TestVariant.class,
                                    item.getModelObject().getId());
                            TestQuestion question = genericManager.getObject(TestQuestion.class,
                                    QuestionInfoPanel.this.getModelObject().getId());
                            genericManager.initialize(question, question.getVariants());

                            int number = variant.getOrderNumber();
                            int totalCount = question.getVariants().size();

                            return number < (totalCount - 1);
                        }

                        @Override
                        protected void onMoveUp(TestVariant entity) {
                            try {
                                testVariantManager.moveVariantOfQuestion(
                                        entity, QuestionInfoPanel.this.getModelObject(), true);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Error while moving entity");
                                throw new WicketRuntimeException("Error while moving entity", e);
                            }
                        }

                        @Override
                        protected void onMoveDown(TestVariant entity) {
                            try {
                                testVariantManager.moveVariantOfQuestion(
                                        entity, QuestionInfoPanel.this.getModelObject(), false);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Error while moving entity");
                                throw new WicketRuntimeException("Error while moving entity", e);
                            }
                        }
                    });
                }
            });

            genericManager.initialize(QuestionInfoPanel.this.getModelObject(),
                    QuestionInfoPanel.this.getModelObject().getResults());

            add(new ListView<TestResult>("resultList",
                    new GenericDBListModel<>(TestResult.class,
                            new ArrayList<>(QuestionInfoPanel.this.getModelObject().getResults()))) {
                @Override
                protected void populateItem(ListItem<TestResult> item) {
                    item.add(new ResultInfoPanel("resultInfoPanel", item.getModel()) {
                        @Override
                        protected void updateList(AjaxRequestTarget target) {
                            QuestionInfoPanel.this.updateList(target);
                        }

                        @Override
                        protected void onEdit(AjaxRequestTarget target, IModel<TestResult> model) {
                            onEditResult(target, model);
                        }
                    });
                }
            });
        }
    }

    protected void onEditVariant(AjaxRequestTarget target, IModel<TestVariant> model) {
    }

    protected void onEditResult(AjaxRequestTarget target, IModel<TestResult> model) {
    }

}
