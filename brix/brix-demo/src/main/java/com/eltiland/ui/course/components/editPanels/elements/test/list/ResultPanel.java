package com.eltiland.ui.course.components.editPanels.elements.test.list;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestResultManager;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AbstractTestPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.result.ResultPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.EntityInfoPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.ResultInfoPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel for Results list for tests.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ResultPanel extends EntityPanel<TestResult> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestResultManager testResultManager;

    /**
     * Panel constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test course item model.
     */
    public ResultPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);
    }

    @Override
    protected AbstractTestPropertyPanel getPropertyPanel(String id) {
        return new ResultPropertyPanel(id, ResultPanel.this.getModel()) {

            @Override
            protected void OnCreate(AjaxRequestTarget target) {
                TestResult variant = new TestResult();

                createEntity(fillEntity(variant));

                propertyDialog.close(target);
                updateList(target);
            }

            @Override
            protected void OnSave(AjaxRequestTarget target) {
                TestResult variant = (TestResult) propertyDialog.getDialogPanel().getModelObject();

                updateEntity(fillEntity(variant));
                propertyDialog.close(target);
                updateList(target);
            }
        };
    }

    @Override
    protected IModel<List<TestResult>> getEntityListModel() {
        return new GenericDBListModel<>(TestResult.class, testResultManager.getResultsForItem(getModelObject()));
    }

    @Override
    protected EntityInfoPanel<TestResult> getEntityInfoPanel(String id, IModel<TestResult> model) {
        return new ResultInfoPanel(id, model) {

            @Override
            protected void updateList(AjaxRequestTarget target) {
                ResultPanel.this.updateList(target);
            }

            @Override
            protected void onEdit(AjaxRequestTarget target, IModel<TestResult> model) {
                propertyDialog.getDialogPanel().initEditMode(model.getObject());
                propertyDialog.show(target);
            }
        };
    }
}
