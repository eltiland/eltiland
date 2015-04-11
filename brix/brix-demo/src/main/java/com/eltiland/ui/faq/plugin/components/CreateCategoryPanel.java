package com.eltiland.ui.faq.plugin.components;

import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating new faq category
 *
 * @author Pavel Androschuk
 */

public class CreateCategoryPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<FaqCategory> {

    private IDialogSimpleNewCallback.IDialogActionProcessor callback;

    private ELTTextArea editor = new ELTTextArea("editor", new ResourceModel("nameLabel"), new Model<String>(), true);

    /**
     * Panel constructor.
     *
     * @param id panel's ID.
     */
    public CreateCategoryPanel(String id) {
        super(id);

        editor.addMaxLengthValidator(80);
        form.setMultiPart(true);
        form.add(editor);
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        switch (event) {
            case Save:
                FaqCategory faqCategory = new FaqCategory();
                faqCategory.setName(editor.getModelObject());
                callback.process(new GenericDBModel(FaqCategory.class, faqCategory), target);
                break;
        }
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<FaqCategory> callback) {
        this.callback = callback;
    }

    @Override
    protected void onBeforeRender() {
        editor.setModelObject(null);
        super.onBeforeRender();
    }
}
