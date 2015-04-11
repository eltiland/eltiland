package com.eltiland.ui.course.components.editPanels.elements.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.validators.TestValidator;
import com.eltiland.exceptions.TestException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.course.components.editPanels.elements.AbstractCourseItemEditPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AttemptLimitPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.list.QuestionPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.list.ResultPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.list.VariantPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Simple test edit panel for courses.
 *
 * @author Aleksey Plotnikov
 */
public class TestEditPanel extends AbstractCourseItemEditPanel<TestCourseItem> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestValidator testValidator;

    private WebMarkupContainer errorContainer = new WebMarkupContainer("errorContainer") {
        @Override
        public boolean isVisible() {
            try {
                testValidator.isTestValid(TestEditPanel.this.getModelObject());
            } catch (TestException e) {
                errorMessage.setDefaultModelObject(e.getMessage());
                return true;
            }
            return false;
        }
    };

    private Label errorMessage = new Label("errorMessage", new Model<String>());

    /**
     * Default constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test course item.
     */
    public TestEditPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);

        genericManager.initialize(testCourseItemIModel.getObject(), testCourseItemIModel.getObject().getQuestions());
        genericManager.initialize(testCourseItemIModel.getObject(), testCourseItemIModel.getObject().getVariants());
        genericManager.initialize(testCourseItemIModel.getObject(), testCourseItemIModel.getObject().getResults());

        add(new QuestionPanel("questionPanel", testCourseItemIModel) {
            @Override
            protected void updateTestValidator(AjaxRequestTarget target) {
                target.add(errorContainer);
            }
        });
        add(new VariantPanel("variantPanel", testCourseItemIModel) {
            @Override
            protected void updateTestValidator(AjaxRequestTarget target) {
                target.add(errorContainer);
            }
        });
        add(new ResultPanel("resultPanel", testCourseItemIModel) {
            @Override
            protected void updateTestValidator(AjaxRequestTarget target) {
                target.add(errorContainer);
            }
        });
        add(new AttemptLimitPanel("attemptLimitPanel", testCourseItemIModel) {
            @Override
            public boolean isVisible() {
                genericManager.initialize(getModelObject(), getModelObject().getCourseFull());
                return getModelObject().getCourseFull() != null;
            }
        });

        add(errorContainer.setOutputMarkupPlaceholderTag(true));
        errorContainer.add(errorMessage);
    }

    @Override
    protected boolean showActions() {
        return true;
    }
}
