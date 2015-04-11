package com.eltiland.ui.common.components.dialog.callback;

import com.eltiland.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onDelete" callback.
 *
 * @param <T> type of deleted object.
 */
public interface IDialogDeleteCallback<T extends Identifiable> {
    /**
     * @param callback New callback object.
     */
    void setDeleteCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogDeleteCallback.
     *
     * @param <T> type of deleted entity.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "Delete" action.
         *
         * @param target ajax target
         */
        void process(AjaxRequestTarget target);
    }
}
