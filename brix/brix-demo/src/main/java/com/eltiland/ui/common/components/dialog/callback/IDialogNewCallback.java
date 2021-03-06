package com.eltiland.ui.common.components.dialog.callback;

import com.eltiland.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onNew" callback.
 *
 * @param <T> type of created object.
 */
public interface IDialogNewCallback<T extends Identifiable> {
    /**
     * @param callback New callback object.
     * @see IDialogSelectCallback
     */
    void setNewCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogNewCallback.
     *
     * @param <T> type of created entity.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "add new" action.
         *
         * @param model  object model for new created object
         * @param target ajax target
         */
        void process(IModel<T> model, AjaxRequestTarget target);
    }
}
