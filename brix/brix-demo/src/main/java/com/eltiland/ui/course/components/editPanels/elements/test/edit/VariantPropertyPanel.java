package com.eltiland.ui.course.components.editPanels.elements.test.edit;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestVariant;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Set;

/**
 * Test variants property panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VariantPropertyPanel extends AbstractTestPropertyPanel<TestVariant> {

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    /**
     * Create mode constructor.
     *
     * @param id markup id.
     */
    public VariantPropertyPanel(String id, IModel<TestCourseItem> itemModel) {
        super(id, new GenericDBModel<>(TestVariant.class));
        this.itemModel = itemModel;
    }

    @Override
    protected FormComponent getAdditionalComponent() {
        return new ELTTextField<>("number", new ResourceModel("number"),
                new Model<Integer>(), Integer.class, true);
    }


    @Override
    protected TestVariant fillEntity(TestVariant object) {
        object.setNumber((Integer) additionalField.getModelObject());

        super.fillEntity(object);
        int totalCount = 0;
        if (object.getQuestion() != null) {
            genericManager.initialize(object, object.getQuestion());
            genericManager.initialize(object.getQuestion(), object.getQuestion().getVariants());
            totalCount = object.getQuestion().getVariants().size();
        } else {
            genericManager.initialize(object.getItem(), object.getItem().getVariants());
            totalCount = object.getItem().getVariants().size();
        }
        object.setOrderNumber(totalCount);

        return object;
    }

    @Override
    public void initCreateMode() {
        super.initCreateMode();
        additionalField.setModelObject(null);
    }

    @Override
    public void initEditMode(TestVariant entity) {
        mode = true;
        setModelObject(entity);
        super.initEditMode(entity);

        additionalField.setModelObject(entity.getNumber());
    }

    @Override
    protected AbstractFormValidator getFormValidator() {
        return new AbstractFormValidator() {
            @Override
            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{additionalField};
            }

            @Override
            public void validate(Form form) {
                int value = (Integer) additionalField.getConvertedInput();
                String text = textField.getConvertedInput();

                if (value < 0) {
                    this.error(additionalField, "negativeError");
                }

                // top level
                Set<TestVariant> variants;
                if (parentModel.getObject() == null) {
                    variants = courseItemManager.initializeTestItem(itemModel.getObject()).getVariants();
                } else {
                    genericManager.initialize(parentModel.getObject(), parentModel.getObject().getVariants());
                    variants = parentModel.getObject().getVariants();
                }

                for (TestVariant variant : variants) {
                    String vText = variant.getValue();

                    TestVariant currentObject = VariantPropertyPanel.this.getModelObject();
                    if (currentObject == null || (!(variant.getId().equals(currentObject.getId())))) {
                        if (vText.equals(text)) {
                            this.error(textField, "variantExistsError");
                        }
                    }
                }

            }
        };
    }
}
