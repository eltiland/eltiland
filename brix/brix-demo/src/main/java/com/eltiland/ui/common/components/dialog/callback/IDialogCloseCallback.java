package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onClose" callback.
 */
public interface IDialogCloseCallback {
    /**
     * @param callback New callback object.
     * @see IDialogSelectCallback
     */
    void setCloseCallback(IDialogActionProcessor callback);

    /**
     * Callback processor for IDialogCloseCallback.
     */
    public interface IDialogActionProcessor extends Serializable {
        /**
         * Process "close" action.
         *
         * @param target ajax target
         */
        void process(AjaxRequestTarget target);
    }
}