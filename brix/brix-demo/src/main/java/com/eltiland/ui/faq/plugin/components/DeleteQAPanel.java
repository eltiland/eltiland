package com.eltiland.ui.faq.plugin.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for removing QA.
 *
 * @author Aleksey PLotnikov.
 */
public abstract class DeleteQAPanel extends BaseEltilandPanel {
    /**
     * Panel constructor.
     *
     * @param id panel's ID.
     */
    public DeleteQAPanel(String id) {
        super(id);

        EltiAjaxLink deleteLink = new EltiAjaxLink("deleteLink") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("deleteApplyMessage")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                OnDelete(target);
            }
        };
        add(deleteLink);

        WebMarkupContainer deleteImage = new WebMarkupContainer("deleteImage");
        deleteLink.add(deleteImage);
    }

    public abstract void OnDelete(AjaxRequestTarget target);
}
