package com.eltiland.ui.common.components.dialog.selector;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Common reusable selector dialog for select single entities. For construction dialogs should be specified
 * SelectorPanel, which responsible for dialogs view and behaviour.<p/>
 * Selector dialogs used as wrapper, which provide
 * dialog constriction and return selected entity by implementing method
 * {@link #onSelect(org.apache.wicket.model.IModel, org.apache.wicket.ajax.AjaxRequestTarget)}. Dialog close behaviour
 * can be override in {@link #onSelected(org.apache.wicket.ajax.AjaxRequestTarget)} and
 * {@link #onClose(org.apache.wicket.ajax.AjaxRequestTarget)}.<p/>
 * For construct search dialogs should be implemented method {@link #createDialogPanel(String)}.
 *
 * @param <T> entity type
 */
public abstract class SelectorDialog<T extends Identifiable> extends Dialog<SelectorPanel<T>> {
    /**
     * Default constructor.
     *
     * @param id wicket component id
     */
    public SelectorDialog(String id) {
        super(id, UIConstants.DIALOG_BIG_WIDTH, UIConstants.DIALOG_MEDIUM_HEIGHT);
    }

    @Override
    public void registerCallback(SelectorPanel<T> panel) {
        super.registerCallback(panel);

        panel.setSelectCallback(new IDialogSelectCallback.IDialogActionProcessor<T>() {
            public void process(IModel<T> iModel, AjaxRequestTarget target) {
                onSelect(iModel, target);
                onSelected(target);
            }
        });
    }

    @Override
    public void onClose(AjaxRequestTarget target) {
        getDialogPanel().resetSearchPattern();

        super.onClose(target);
    }

    /**
     * Implement this method for get selected model and perform post select actions using ajax target.
     *
     * @param model  selected model
     * @param target ajax target
     */
    public abstract void onSelect(IModel<T> model, AjaxRequestTarget target);

    /**
     * After selected default behaviour.<p/>
     * Override this method for change default action after select.
     *
     * @param target ajax target
     */
    public void onSelected(AjaxRequestTarget target) {
        close(target);
    }
}
