package com.eltiland.ui.tags.components.selector;

import com.eltiland.model.tags.Tag;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Internal panel for output tag and checking it.
 *
 * @author Aleksey Plotnikov.
 */
abstract class TagPanel extends BaseEltilandPanel<Tag> {

    /**
     * Panel constrctor.
     *
     * @param id        markup id.
     * @param tagIModel tag model.
     * @param value     iT TRUE - checkbox will be initially checked.
     */
    protected TagPanel(String id, IModel<Tag> tagIModel, boolean value) {
        super(id, tagIModel);

        ELTAjaxCheckBox checkBox = new ELTAjaxCheckBox(
                "tagCheckBox", new Model<>(getModelObject().getName()), new Model<>(value)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                TagPanel.this.onUpdate(target, getModel());
            }
        };

        add(checkBox);
    }

    protected abstract void onUpdate(AjaxRequestTarget target, IModel<Boolean> valueModel);
}
