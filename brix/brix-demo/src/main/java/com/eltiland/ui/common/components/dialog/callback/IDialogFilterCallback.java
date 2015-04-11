package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Callback interface for filter panels.
 *
 * @author Pavel Knorr
 * @version 1.0
 */
public interface IDialogFilterCallback {

    /**
     * @param callback New callback object.
     */
    void setFilterCallback(IDialogActionProcessor callback);

    /**
     * Callback processor for IDialogFilterCallback.
     */
    public interface IDialogActionProcessor extends Serializable {
        /**
         * Process "filter" action.
         *
         * @param target ajax
         */
        void process(AjaxRequestTarget target);
    }

}
