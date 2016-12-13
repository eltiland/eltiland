package com.eltiland.ui.course.edit;

import com.eltiland.bl.course.audio.ELTAudioItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.audio.ELTAudioCourseItem;
import com.eltiland.model.course2.content.audio.ELTAudioItem;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing audio course item.
 *
 * @author Aleksey Plotnikov.
 */
public class AudioCourseItemPanel extends AbstractCourseItemPanel<ELTAudioCourseItem> {

    private ELTTextArea descriptionField =
            new ELTTextArea("description", new ResourceModel("description"), new Model<String>());

    private ELTTextField<String> linkField =
            new ELTTextField<>("link", new ResourceModel("link"), new Model<String>(), String.class, true);

    private IModel<ELTAudioItem> audioItemIModel = new LoadableDetachableModel<ELTAudioItem>() {
        @Override
        protected ELTAudioItem load() {
            return audioItemManager.get(getModelObject());
        }
    };

    @SpringBean
    private ELTAudioItemManager audioItemManager;

    public AudioCourseItemPanel(String id, IModel<ELTAudioCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        Form form = new Form("form");

        form.add(descriptionField);
        form.add(linkField);

        final boolean hasAudio = audioItemIModel.getObject() != null;
        if (hasAudio) {
            descriptionField.setModelObject(audioItemIModel.getObject().getDescription());
            linkField.setModelObject(audioItemIModel.getObject().getLink());
        }

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form form) {
                ELTAudioItem audioItem = (!hasAudio) ? new ELTAudioItem() : audioItemIModel.getObject();
                audioItem.setItem(AudioCourseItemPanel.this.getModelObject());
                audioItem.setLink(linkField.getModelObject());
                audioItem.setDescription(descriptionField.getModelObject());

                try {
                    if (hasAudio) {
                        audioItemManager.update(audioItem);
                    } else {
                        audioItemManager.create(audioItem);
                    }
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), ajaxRequestTarget);
                }

                ELTAlerts.renderOKPopup(getString("saved"), ajaxRequestTarget);
            }
        });

        add(form);
    }
}
