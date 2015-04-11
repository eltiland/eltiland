package com.eltiland.ui.common.components.dialog.callback;

import com.eltiland.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onUpdate" callback.
 *
 * @param <T> type of updated object.
 */
public interface IDialogUpdateCallback<T extends Identifiable> {
    /**
     * @param callback New callback object.
     * @see IDialogSelectCallback
     */
    void setUpdateCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogUpdateCallback.
     *
     * @param <T> type of updated entity.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "update" action.
         *
         * @param model  model for updated object
         * @param target ajax target
         */
        void process(IModel<T> model, AjaxRequestTarget target);
    }
}
