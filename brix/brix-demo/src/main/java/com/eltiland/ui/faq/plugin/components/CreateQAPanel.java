package com.eltiland.ui.faq.plugin.components;

import com.eltiland.bl.FaqCategoryManager;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.faq.components.FaqCategoryList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating new QA.
 *
 * @author Aleksey PLotnikov.
 */
public class CreateQAPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<Faq> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CreateQAPanel.class);

    @SpringBean
    private FaqCategoryManager faqCategoryManager;

    private IDialogSimpleNewCallback.IDialogActionProcessor callback;

    private ELTTextArea qEditor = new ELTTextArea("qEditor", new ResourceModel("questionLabel"),
            new Model<String>(), true);
    private ELTTextArea aEditor = new ELTTextArea("aEditor", new ResourceModel("answerLabel"),
            new Model<String>(), true);

    FaqCategoryList categoryChoice = new FaqCategoryList("category") {
        @Override
        public void onCategoryUpdate(AjaxRequestTarget target, IModel<FaqCategory> model) {
        }
    };

    /**
     * Panel constructor.
     *
     * @param id panel's ID.
     */
    public CreateQAPanel(String id) {
        super(id);

        categoryChoice.getListObject().setRequired(true);

        aEditor.addMaxLengthValidator(2048);
        qEditor.addMaxLengthValidator(2048);

        form.setMultiPart(true);
        form.add(qEditor);
        form.add(aEditor);
        form.add(categoryChoice);
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Save);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        switch (event) {
            case Save:
                Faq faq = new Faq();
                faq.setQuestion(qEditor.getModelObject());
                faq.setAnswer(aEditor.getModelObject());
                faq.setCategory(categoryChoice.getCategoryModel().getObject());
                callback.process(new GenericDBModel(Faq.class, faq), target);
                break;
        }
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<Faq> callback) {
        this.callback = callback;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        qEditor.setModelObject(null);
        aEditor.setModelObject(null);
    }
}
