package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onConfirm" callback.
 */
public interface IDialogConfirmCallback {
    /**
     * @param callback New callback object.
     */
    void setConfirmCallback(IDialogActionProcessor callback);

    /**
     * Callback processor for IDialogConfirmCallback.
     */
    public interface IDialogActionProcessor extends Serializable {
        /**
         * Process "Confirm" action.
         *
         * @param target ajax target
         */
        void process(AjaxRequestTarget target);
    }
}
