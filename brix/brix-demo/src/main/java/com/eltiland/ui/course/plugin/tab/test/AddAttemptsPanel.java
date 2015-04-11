package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for adding new attempts to user.
 *
 * @author Aleksey Plotnikov.
 */
public class AddAttemptsPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<Integer> {

    private IDialogActionProcessor<Integer> callBack;

    private ELTTextField<Integer> attemptsPanel = new ELTTextField<>(
            "attemptsField", new ResourceModel("addAction"), new Model<Integer>(), Integer.class, true);

    public AddAttemptsPanel(String id) {
        super(id);
        form.add(attemptsPanel);
        attemptsPanel.addValidator(new IValidator<Integer>() {
            @Override
            public void validate(IValidatable<Integer> validatable) {
                if (validatable.getValue() <= 0) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("countError");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Add));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        callBack.process(attemptsPanel.getModel(), target);
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<Integer> callback) {
        this.callBack = callback;
    }
}
