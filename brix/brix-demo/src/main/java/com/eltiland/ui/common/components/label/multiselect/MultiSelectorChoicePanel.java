package com.eltiland.ui.common.components.label.multiselect;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author knorr
 * @version 1.0
 * @since 8/14/12
 */
public class MultiSelectorChoicePanel<T extends Identifiable> extends Panel implements IDialogSelectCallback<T> {

    private boolean isReadOnly = false;

    private final EltiAjaxLink eltiAjaxLink = new EltiAjaxLink("cross") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            if (callback != null) {
                callback.process(MultiSelectorChoicePanel.this.getDefaultModel(), target);
            }
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!isReadOnly());
        }
    };

    public MultiSelectorChoicePanel(String id, IModel<T> selectedObjectModel, Component component) {
        super(id, selectedObjectModel);
        setOutputMarkupId(true);
        add(component);
        add(eltiAjaxLink);
    }

    public static String getInnerPanelMarkupId() {
        return "element";
    }

    private IDialogActionProcessor callback;

    @Override
    public void setSelectCallback(IDialogActionProcessor<T> callback) {
        this.callback = callback;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }
}
