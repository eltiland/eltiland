package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Callback interface for search panels with support "onSearch" callback.
 *
 * @param <T> type of search pattern
 */
public interface IDialogSearchCallback<T> {
    /**
     * @param callback New callback object.
     */
    void setSearchCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogNewCallback.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "search" action.
         *
         * @param searchCriteria model for search pattern
         * @param target         ajax target
         */
        void process(IModel<T> searchCriteria, AjaxRequestTarget target);
    }
}
