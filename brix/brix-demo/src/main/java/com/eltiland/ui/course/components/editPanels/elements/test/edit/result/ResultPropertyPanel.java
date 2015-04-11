package com.eltiland.ui.course.components.editPanels.elements.test.edit.result;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestResultManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AbstractTestPropertyPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Test results property panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ResultPropertyPanel extends AbstractTestPropertyPanel<TestResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultPropertyPanel.class);

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestJumpManager testJumpManager;
    @SpringBean
    private TestResultManager testResultManager;

    /**
     * Create mode constructor.
     *
     * @param id markup id.
     */
    public ResultPropertyPanel(String id, IModel<TestCourseItem> itemModel) {
        super(id, new GenericDBModel<>(TestResult.class), itemModel);
    }

    @Override
    protected FormComponent getAdditionalComponent() {
        return new ELTTextField<>("minValue", new ResourceModel("minNumber"),
                new Model<Integer>(), Integer.class);
    }

    @Override
    protected FormComponent getAdditionalComponent2() {
        return new ELTTextField<>("maxValue", new ResourceModel("maxNumber"),
                new Model<Integer>(), Integer.class);
    }

    @Override
    protected FormComponent getAdditionalComponent3() {
        return new JumpsPanel("jumpPanel", new GenericDBListModel<>(TestJump.class), itemModel);
    }

    @Override
    protected FormComponent getAdditionalComponent4() {
        return new ELTAjaxCheckBox("final", new ResourceModel("finalLabel"), new Model<>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                additionalField3.setEnabled(!getConvertedInput());
                target.add(additionalField3);
            }
        };
    }

    @Override
    protected FormComponent getAdditionalComponent5() {
        return new ELTTextField<>("value", new ResourceModel("value"), new Model<Integer>(), Integer.class);
    }

    @Override
    protected FormComponent getAdditionalComponent6() {
        return new CheckBox("right", new Model<>(false));
    }

    @Override
    protected TestResult fillEntity(TestResult object) {
        Integer value = (Integer) additionalField5.getModelObject();
        if (value != null) {
            object.setMaxValue(value);
            object.setMinValue(value);
        } else {
            object.setMinValue((Integer) additionalField.getModelObject());
            object.setMaxValue((Integer) additionalField2.getModelObject());
        }

        boolean isFinish = (Boolean) additionalField4.getModelObject();
        object.setJumpFinish(isFinish);

        boolean isRight = (Boolean) additionalField6.getModelObject();
        object.setRightResult(isRight);

        return super.fillEntity(object);
    }

    @Override
    protected TestResult createEntity(TestResult object) {
        TestResult result = super.createEntity(object);
        fillJumps(result);
        if (additionalField6.isVisible()) {
            fillRightFlag(result);
        }
        return result;
    }

    @Override
    protected TestResult updateEntity(TestResult object) {
        TestResult result = super.updateEntity(object);
        fillJumps(result);
        if (additionalField6.isVisible()) {
            fillRightFlag(result);
        }
        return result;
    }

    private void fillJumps(TestResult result) {
        boolean isFinish = (Boolean) additionalField4.getModelObject();

        if (!isFinish) {
            List<TestJump> jumps = ((JumpsPanel) additionalField3).getModelObject();
            for (TestJump jump : jumps) {
                jump.setItem(itemModel.getObject());
                if (jump.getResult() == null) {
                    jump.setResult(result);
                }
                try {
                    testJumpManager.updateJump(jump);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update jump", e);
                    throw new WicketRuntimeException("Cannot update jump", e);
                }
            }
        }
    }

    private void fillRightFlag(TestResult result) {
        try {
            testResultManager.updateRightFlag(itemModel.getObject(), result);
        } catch (EltilandManagerException e) {
            LOGGER.error("Cannot save result", e);
            throw new WicketRuntimeException("Cannot save result", e);
        }
    }

    @Override
    public void initCreateMode() {
        super.initCreateMode();
        additionalField.setModelObject(null);
        additionalField2.setModelObject(null);
        additionalField3.setModelObject(null);
        additionalField4.setModelObject(false);
        additionalField5.setModelObject(null);
        additionalField6.setModelObject(false);
        additionalField3.setEnabled(true);
        ((JumpsPanel) additionalField3).initCreateMode();
    }

    @Override
    public void initEditMode(TestResult entity) {
        mode = true;
        setModelObject(entity);
        super.initEditMode(entity);

        if (entity.getMaxValue() == entity.getMinValue()) {
            additionalField5.setModelObject(entity.getMaxValue());
            additionalField.setModelObject(null);
            additionalField2.setModelObject(null);
        } else {
            additionalField.setModelObject(entity.getMinValue());
            additionalField2.setModelObject(entity.getMaxValue());
            additionalField5.setModelObject(null);
        }

        genericManager.initialize(entity, entity.getJumps());
        ((JumpsPanel) additionalField3).initEditMode(new ArrayList<>(entity.getJumps()),
                new GenericDBModel<>(TestResult.class, entity));

        boolean isFinish = entity.isJumpFinish();
        additionalField4.setModelObject(isFinish);
        additionalField3.setEnabled(!isFinish);
        additionalField6.setModelObject(entity.isRightResult());
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (getParentModel() != null && getParentModel().getObject() != null) {
            additionalField3.setVisible(true);
            additionalField4.setVisible(true);
            additionalField6.setVisible(false);
        } else {
            additionalField3.setVisible(false);
            additionalField4.setVisible(false);
            additionalField6.setVisible(true);
        }
    }

    @Override
    protected boolean isRequired() {
        return false;
    }

    @Override
    protected AbstractFormValidator getFormValidator() {
        return new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{
                        textField, additionalField, additionalField2,
                        additionalField3, additionalField4, additionalField5};
            }

            @Override
            public void validate(Form form) {
                Integer min = (Integer) additionalField.getConvertedInput();
                Integer max = (Integer) additionalField2.getConvertedInput();
                Integer val = (Integer) additionalField5.getConvertedInput();

                if (!((val != null && max == null && min == null) || (val == null && max != null && min != null))) {
                    this.error(additionalField, "intervalError");
                    this.error(additionalField2, "intervalError");
                    this.error(additionalField5, "intervalError");
                    return;
                }

                if ((min != null) && (min < 0)) {
                    this.error(additionalField, "negativeError");
                }

                if ((max != null) && (max < 0)) {
                    this.error(additionalField2, "negativeError");
                }

                if ((val != null) && (val < 0)) {
                    this.error(additionalField5, "negativeError");
                }

                if ((max != null) && (min != null) && (min >= max)) {
                    this.error(additionalField, "maxminError");
                    this.error(additionalField2, "maxminError");
                }

                String text = textField.getConvertedInput();
                if (text == null || text.isEmpty()) {
                    List<TestJump> jumps = ((JumpsPanel) additionalField3).getModelObject();
                    boolean hasNoCondition = false;

                    for (TestJump testJump : jumps) {
                        genericManager.initialize(testJump, testJump.getPrevs());
                        if (testJump.getPrevs().isEmpty()) {
                            hasNoCondition = true;
                        }
                    }

                    boolean isFinish = (Boolean) additionalField4.getConvertedInput();
                    boolean hasJumps = !(jumps.isEmpty());
                    if ((!hasJumps && !isFinish) || (hasJumps && !hasNoCondition && !isFinish)) {
                        if (!((Boolean) additionalField4.getConvertedInput())) {
                            this.error(textField, "noResultError");
                            this.error(additionalField3, "noResultError");
                            this.error(additionalField4, "noResultError");
                        }
                    }
                }

                // top level
                Set<TestResult> results;
                if (parentModel.getObject() == null) {
                    results = courseItemManager.initializeTestItem(itemModel.getObject()).getResults();
                } else {
                    genericManager.initialize(parentModel.getObject(), parentModel.getObject().getResults());
                    results = parentModel.getObject().getResults();
                }

                if (val != null) {
                    max = val;
                    min = val;
                }

                for (TestResult result : results) {
                    int rMin = result.getMinValue();
                    int rMax = result.getMaxValue();

                    TestResult currentObject = ResultPropertyPanel.this.getModelObject();

                    if (currentObject == null || (!(currentObject.getId().equals(result.getId())))) {
                        if (isWithinInterval(rMin, rMax, min) || isWithinInterval(rMin, rMax, max)) {
                            if (val != null) {
                                this.error(additionalField5, "intersectError");
                            } else {
                                this.error(additionalField, "intersectError");
                                this.error(additionalField2, "intersectError");
                            }
                        }
                    }
                }
            }

            private boolean isWithinInterval(int min, int max, int check) {
                return (check >= min) && (check <= max);
            }
        };
    }
}
