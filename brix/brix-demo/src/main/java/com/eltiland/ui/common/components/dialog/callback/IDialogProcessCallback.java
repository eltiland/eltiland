package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * @author knorr
 * @version 1.0
 * @since 8/2/12
 */
public interface IDialogProcessCallback<T extends Object> {

    /**
     * @param callback process callback object.
     * @see IDialogSelectCallback
     */
    void setProcessCallback(IDialogActionProcessor<T> callback);

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
