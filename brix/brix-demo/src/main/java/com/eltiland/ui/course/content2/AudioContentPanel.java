
package com.eltiland.ui.course.content2;

import com.eltiland.bl.course.audio.ELTAudioItemManager;
import com.eltiland.model.course2.content.audio.ELTAudioCourseItem;
import com.eltiland.model.course2.content.audio.ELTAudioItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output audio of course.
 *
 * @author Aleksey Plotnikov.
 */
public class AudioContentPanel extends AbstractCourseContentPanel<ELTAudioCourseItem> {

    @SpringBean
    private ELTAudioItemManager audioItemManager;

    private IModel<ELTAudioItem> audioItemIModel = new LoadableDetachableModel<ELTAudioItem>() {
        @Override
        protected ELTAudioItem load() {
            return audioItemManager.get(getModelObject());
        }
    };

    private IModel<Boolean> hasModel = new LoadableDetachableModel<Boolean>() {
        @Override
        protected Boolean load() {
            return audioItemIModel.getObject() != null;
        }
    };

    public AudioContentPanel(String id, IModel<ELTAudioCourseItem> eltAudioCourseItemIModel) {
        super(id, eltAudioCourseItemIModel);

        Label description = new Label("audio_description", new Model<String>()) {
            @Override
            public boolean isVisible() {
                return hasModel.getObject();
            }
        };

        ExternalLink link = new ExternalLink("link", new Model<String>());

        add(link);
        add(description).setOutputMarkupId(true);

        if (hasModel.getObject()) {
            description.setDefaultModelObject(audioItemIModel.getObject().getDescription());
            link.setDefaultModelObject(audioItemIModel.getObject().getLink());
        }
    }
}

