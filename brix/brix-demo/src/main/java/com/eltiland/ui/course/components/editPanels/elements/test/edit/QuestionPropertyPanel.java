package com.eltiland.ui.course.components.editPanels.elements.test.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Test question property panel.
 *
 * @author Aleksey Plotnikov.
 */

public abstract class QuestionPropertyPanel extends AbstractTestPropertyPanel<TestQuestion> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestQuestionManager testQuestionManager;

    private boolean showCheckBox = false;
    private boolean sectionChecked = false;

    /**
     * Panel constructor.
     *
     * @param id        markup id.
     * @param itemModel test course item.
     */
    protected QuestionPropertyPanel(String id, IModel<TestCourseItem> itemModel) {
        super(id, new GenericDBModel<>(TestQuestion.class));
        this.itemModel = itemModel;
        showCheckBox = false;
    }

    @Override
    public void initCreateMode() {
        boolean isSection = getTextHeader().equals("section");
        showCheckBox = !(isSection);
        textField.setValueRequired(!isSection);
        super.initCreateMode();
    }

    @Override
    public void initEditMode(TestQuestion entity) {
        mode = true;
        showCheckBox = false;
        setModelObject(entity);
        super.initEditMode(entity);
    }

    @Override
    protected FormComponent getAdditionalComponent() {
        return new ELTAjaxCheckBox("createSectionFlag", new ResourceModel("createSection"), new Model<>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                sectionChecked = getConvertedInput();
            }
        };
    }

    @Override
    protected boolean isAdditionalControlVisible() {
        return showCheckBox;
    }

    protected boolean getIsCheckedSection() {
        return showCheckBox && sectionChecked;
    }

    public void setCheckBoxShown(boolean isShown) {
        showCheckBox = isShown;
    }
}
