package com.eltiland.ui.common.components.dialog.callback;

import com.eltiland.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Callback interface for panels with support "onSelect" callback.
 *
 * @param <T> type of selected object.
 */
public interface IDialogSelectCallback<T extends Identifiable> {
    /**
     * This method will called by dialog for construct callback processor and submit it to panel.
     * Callback processor used for translate panel event into dialog event.
     * <pre>
     *  Should be implemented in dialog
     *      ...
     *      dialogPanel.setSelectCallback(<b>new IDialogSelectCallback.IDialogActionProcessor()</b> {
     *          public void process(IModel<Cargo> model, AjaxRequestTarget target) {
     *            <b>dialog.onSelect(model, target);</b>
     *          }
     *      });
     *      ...
     *  Should be used in panel
     *      ...
     *      private IDialogActionProcessor <b>onSelectCallback</b>;
     *      public IDialogActionProcessor<T> setSelectCallback(IDialogActionProcessor<T> callback) {
     *          <b>this.onSelectCallback = callback</b>;
     *      }
     *      ...
     *      &#64;Override
     *      protected void onClick(AjaxRequestTarget target) {
     *          Cargo selectedObject =
     *            manager.getObjects(Cargo.class, (Long) getDefaultModelObject());
     *          IModel<Cargo> selectedModel =
     *            new GenericDBModel<Cargo>(Cargo.class, selectedObject);
     *
     *         <b>onSelectCallback.process(selectedModel, target);</b>
     *      }
     *
     *
     * </pre>
     *
     * @param callback New callback object.
     */
    void setSelectCallback(IDialogActionProcessor<T> callback);

    /**
     * Callback processor for IDialogSelectCallback.
     *
     * @param <T> type of selected entity.
     */
    public interface IDialogActionProcessor<T> extends Serializable {
        /**
         * Process "onSelect" action.
         *
         * @param model  selected object model
         * @param target ajax target
         */
        void process(IModel<T> model, AjaxRequestTarget target);
    }
}
