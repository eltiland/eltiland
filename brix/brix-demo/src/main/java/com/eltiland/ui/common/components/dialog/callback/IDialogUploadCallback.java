package com.eltiland.ui.common.components.dialog.callback;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onUpload" callback.
 *
 * @param <T> type of uploaded object.
 */
public interface IDialogUploadCallback<T> {
    /**
     * @param callback New callback object.
     * @see IDialogSelectCallback
     */
    void setUploadCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogUploadCallback.
     *
     * @param <T> type of submitted entity.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "upload" action.
         *
         * @param uploadedFileModel uploaded object model
         * @param target            ajax target
         */
        void process(IModel<T> uploadedFileModel, AjaxRequestTarget target);
    }
}