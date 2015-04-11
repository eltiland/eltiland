package com.eltiland.ui.course.components.editPanels.elements.test.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestEntity;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract test property panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractTestPropertyPanel<T extends TestEntity> extends BaseEltilandPanel<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestPropertyPanel.class);

    protected boolean mode = false;

    @SpringBean
    private GenericManager genericManager;

    protected ELTTextArea textField =
            new ELTTextArea("text", new ResourceModel(getTextHeader()), new Model<String>(), isRequired()) {
                @Override
                protected boolean isFillToWidth() {
                    return true;
                }
            };

    protected FormComponent additionalField;
    protected FormComponent additionalField2;
    protected FormComponent additionalField3;
    protected FormComponent additionalField4;
    protected FormComponent additionalField5;
    protected FormComponent additionalField6;

    protected IModel<TestCourseItem> itemModel = new GenericDBModel<>(TestCourseItem.class);

    private Label headerLabel = new Label("header", getString(mode ? getSaveHeader() : getCreateKey()));

    protected IModel<TestQuestion> parentModel = new GenericDBModel<>(TestQuestion.class);

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            OnCreate(target);
        }

        @Override
        public boolean isVisible() {
            return !getMode();
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            OnSave(target);
        }

        @Override
        public boolean isVisible() {
            return getMode();
        }
    };

    /**
     * Panel constructor.
     *
     * @param id      markup id.
     * @param tiModel entity object model.
     */
    protected AbstractTestPropertyPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);

        addComponents();
    }

    protected AbstractTestPropertyPanel(String id, IModel<T> tiModel, IModel<TestCourseItem> itemModel) {
        super(id, tiModel);
        this.itemModel = itemModel;
        genericManager.initialize(itemModel.getObject(), itemModel.getObject().getQuestions());
        addComponents();
    }

    /**
     * Init panel to edit mode of given entity.
     */
    public void initEditMode(T entity) {
        textField.setModelObject(entity.getTextValue());
        headerLabel.setDefaultModelObject(getString(getSaveHeader()));
    }

    /**
     * Init panel to create mode.
     */
    public void initCreateMode() {
        mode = false;
        setModelObject(null);
        textField.setModelObject(null);
        headerLabel.setDefaultModelObject(getString(getCreateKey()));
    }

    /**
     * Return TRUE, if panel in edit mode.
     */
    protected boolean getMode() {
        return mode;
    }

    /**
     * Return additional data form component.
     */
    protected FormComponent getAdditionalComponent() {
        return null;
    }

    /**
     * Return second additional data form component.
     */
    protected FormComponent getAdditionalComponent2() {
        return null;
    }

    /**
     * Return third additional data form component.
     */
    protected FormComponent getAdditionalComponent3() {
        return null;
    }

    /**
     * Return fourth additional data form component.
     */
    protected FormComponent getAdditionalComponent4() {
        return null;
    }

    /**
     * Return fivth additional data form component.
     */
    protected FormComponent getAdditionalComponent5() {
        return null;
    }

    /**
     * Return sixth additional data form component.
     */
    protected FormComponent getAdditionalComponent6() {
        return null;
    }

    /**
     * Return validator for form.
     */
    protected AbstractFormValidator getFormValidator() {
        return null;
    }

    /**
     * Create entity callback.
     */
    protected abstract void OnCreate(AjaxRequestTarget target);

    /**
     * Save entity callback.
     */
    protected abstract void OnSave(AjaxRequestTarget target);

    /**
     * Save and return new entity.
     */
    protected T createEntity(T object) {
        try {
            return genericManager.saveNew(object);
        } catch (ConstraintException e) {
            LOGGER.error("Cannot create course test item", e);
            throw new WicketRuntimeException("Cannot create course test item", e);
        }
    }

    /**
     * Update entity
     */
    protected T updateEntity(T object) {
        try {
            return genericManager.update(object);
        } catch (ConstraintException e) {
            LOGGER.error("Cannot update course test item", e);
            throw new WicketRuntimeException("Cannot update course test item", e);
        }
    }

    /**
     * Fill new entity by entered general data.
     */
    protected T fillEntity(T object) {
        object.setTextValue(textField.getModelObject());
        object.setTestItem(itemModel.getObject());
        return object;
    }

    protected String getCreateKey() {
        return "addHeader";
    }

    protected String getSaveHeader() {
        return "saveHeader";
    }

    protected String getTextHeader() {
        return "text";
    }

    protected boolean isAdditionalControlVisible() {
        return true;
    }

    private WebMarkupContainer additionalContainer = new WebMarkupContainer("additional") {
        @Override
        public boolean isVisible() {
            return isAdditionalControlVisible();
        }
    };

    private void addComponents() {
        add(headerLabel);

        Form form = new Form("form");

        form.add(createButton);
        form.add(saveButton);
        form.add(textField);
        form.add(new FormRequired("required"));
        form.add(additionalContainer);

        AbstractFormValidator validator = getFormValidator();
        if (validator != null) {
            form.add(validator);
        }

        additionalField = getAdditionalComponent();
        if (additionalField != null) {
            additionalContainer.add(additionalField);
        }

        additionalField2 = getAdditionalComponent2();
        if (additionalField2 != null) {
            additionalContainer.add(additionalField2);
        }

        additionalField3 = getAdditionalComponent3();
        if (additionalField3 != null) {
            additionalContainer.add(additionalField3.setOutputMarkupId(true));
        }

        additionalField4 = getAdditionalComponent4();
        if (additionalField4 != null) {
            additionalContainer.add(additionalField4);
        }

        additionalField5 = getAdditionalComponent5();
        if (additionalField5 != null) {
            additionalContainer.add(additionalField5);
        }

        additionalField6 = getAdditionalComponent6();
        if (additionalField6 != null) {
            additionalContainer.add(additionalField6);
        }

        add(form);
    }

    public IModel<TestQuestion> getParentModel() {
        return parentModel;
    }

    public void setParentModel(IModel<TestQuestion> parentModel) {
        this.parentModel = parentModel;
    }

    protected boolean isRequired() {
        return true;
    }
}
