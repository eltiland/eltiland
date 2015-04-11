package com.eltiland.ui.library.panels.type;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.library.LibraryVideoRecord;
import com.eltiland.ui.common.components.video.YoutubeLinkVideoPlayer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Panel for creating/editing document entity of the library.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VideoPropertyPanel extends GeneralPropertyPanel<LibraryVideoRecord> {

    private YoutubeLinkVideoPlayer player = new YoutubeLinkVideoPlayer("player", new Model<String>()) {
        @Override
        protected boolean isRequiredField() {
            return true;
        }
    };

    /**
     * Ctor for creating new entity.
     *
     * @param id markup id.
     */
    public VideoPropertyPanel(String id) {
        super(id);
        addComponents();
    }

    /**
     * Ctor for editing new entity.
     *
     * @param id                          markup id.
     * @param libraryDocumentRecordIModel document record entity.
     */
    protected VideoPropertyPanel(String id, IModel<LibraryVideoRecord> libraryDocumentRecordIModel) {
        super(id, libraryDocumentRecordIModel);
        addComponents();
        player.setModelObject(getModelObject().getVideoLink());
    }

    private void addComponents() {
        add(player);
    }

    @Override
    protected Class<? extends LibraryRecord> getItemClass() {
        return LibraryVideoRecord.class;
    }

    public String getLink() {
        return player.getVideoObject();
    }
}
