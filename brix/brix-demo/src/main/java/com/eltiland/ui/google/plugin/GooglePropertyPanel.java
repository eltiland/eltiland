package com.eltiland.ui.google.plugin;

import com.eltiland.bl.drive.GooglePageManager;
import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for managing/creating google page.
 *
 * @author Aleksey Plotnikov.
 */
public class GooglePropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<GooglePage>, IDialogUpdateCallback<GooglePage> {

    @SpringBean
    private GooglePageManager googlePageManager;

    private IDialogUpdateCallback.IDialogActionProcessor<GooglePage> updateCallback;
    private IDialogNewCallback.IDialogActionProcessor<GooglePage> createCallback;

    private boolean editMode = false;

    private ELTTextField<String> nameField = new ELTTextField<>(
            "nameField", new ResourceModel("panelName"), new Model<String>(), String.class, true);

    private IModel<GooglePage> googlePageIModel = new GenericDBModel<>(GooglePage.class);

    public GooglePropertyPanel(String id) {
        super(id);
        form.add(nameField);
        nameField.addMaxLengthValidator(128);
        nameField.add(new StringValidator() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                if (googlePageManager.getPageByName(validatable.getValue()) != null) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("errorPageExists");
                        }
                    });
                }
            }
        });
    }

    public void initCreateMode() {
        editMode = false;
        nameField.setModelObject(null);
    }

    public void initEditMode(GooglePage page) {
        editMode = true;
        googlePageIModel.setObject(page);
        nameField.setModelObject(page.getName());
    }

    @Override
    protected String getHeader() {
        return getString(editMode ? "editHeader" : "createHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save, EVENT.Create));
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        return (event.equals(EVENT.Save) && editMode) || (event.equals(EVENT.Create) && !editMode);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Create)) {
            GooglePage page = new GooglePage();
            page.setName(nameField.getModelObject());
            createCallback.process(new GenericDBModel<>(GooglePage.class, page), target);
        } else if (event.equals(EVENT.Save)) {
            GooglePage page = googlePageIModel.getObject();
            page.setName(nameField.getModelObject());
            updateCallback.process(new GenericDBModel<>(GooglePage.class, page), target);
        }
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<GooglePage> callback) {
        this.createCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<GooglePage> callback) {
        this.updateCallback = callback;
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }
}
