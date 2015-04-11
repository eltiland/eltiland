package com.eltiland.ui.common.compositepage.gazeta;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Control for output one magazine "Eltik and friends".
 * Magazine pages must be named as: 1.jpg, 2.jpg... up to 16.jpg, and they must located in the same folder,
 * name of which must be passed as string model to this panel constructor.
 *
 * @author Aleksey Pltonikov.
 */
public class MagazineStaticPanel extends BaseEltilandPanel<String> {

    private IModel<List<Integer>> pagesList = new LoadableDetachableModel<List<Integer>>() {
        @Override
        protected List<Integer> load() {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= 16; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param stringIModel folder name model.
     */
    public MagazineStaticPanel(String id, final IModel<String> stringIModel) {
        super(id, stringIModel);

        String folder = stringIModel.getObject();

        WebMarkupContainer link = new WebMarkupContainer("link");
        link.add(new AttributeAppender("rel", "shadowbox[" + folder + "]"));
        add(link);

        WebMarkupContainer image = new WebMarkupContainer("image");
        image.add(new AttributeAppender("src", "static/newspaper/" + folder + "/1.jpg"));
        link.add(image);


        add(new ListView<Integer>("pageList", pagesList) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                item.add(new LinkPanel("page", stringIModel, item.getModelObject()));
            }
        });
    }

    private class LinkPanel extends BaseEltilandPanel<String> {

        protected LinkPanel(String id, IModel<String> stringIModel, int pageNumber) {
            super(id, stringIModel);

            String folder = stringIModel.getObject();

            WebMarkupContainer image = new WebMarkupContainer("image");
            image.add(new AttributeAppender("href",
                    "static/newspaper/" + folder + "/" + String.valueOf(pageNumber) + ".jpg"));
            image.add(new AttributeAppender("rel", "shadowbox[" + folder + "]"));
            add(image);
        }
    }
}
