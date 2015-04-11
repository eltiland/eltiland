package com.eltiland.ui.common.components.slider;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.Slider;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Slider panel control.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTSliderPanel extends BaseEltilandPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELTSliderPanel.class);

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constrctor.
     *
     * @param id markup id.
     */
    public ELTSliderPanel(String id) {
        super(id);

        add(new ListView<Slider>("imageList", new LoadableDetachableModel<List<? extends Slider>>() {
            @Override
            protected List<? extends Slider> load() {
                return genericManager.getEntityList(Slider.class, "order");
            }
        }) {
            @Override
            protected void populateItem(ListItem<Slider> item) {
                try {
                    File file = fileManager.getFileById(item.getModelObject().getFile().getId());
                    ExternalLink link = new ExternalLink("link", item.getModelObject().getLink());
                    link.add(new Image("image", getImageResource(file)));
                    item.add(link);
                } catch (IOException | ResourceStreamNotFoundException e) {
                    LOGGER.error("Cannot load image", e);
                    throw new WicketRuntimeException("Cannot move image", e);
                }
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(ResourcesUtils.SLIDER_CSS);
        response.renderJavaScriptReference(ResourcesUtils.SLIDER_JS);
    }

    private IResource getImageResource(File file) throws ResourceStreamNotFoundException, IOException {
        IResource imageResource = new ByteArrayResource(file.getType(),
                file.getPreviewBody().getBody(), file.getName());
        return imageResource;
    }
}
