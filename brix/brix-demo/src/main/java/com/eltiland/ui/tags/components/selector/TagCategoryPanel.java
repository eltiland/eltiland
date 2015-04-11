package com.eltiland.ui.tags.components.selector;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal panel for tag category output.
 *
 * @author Aleksey Plotnikov.
 */
abstract class TagCategoryPanel extends BaseEltilandPanel<TagCategory> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TagEntityManager tagEntityManager;
    @SpringBean
    private TagManager tagManager;

    /**
     * Panel constructor.
     *
     * @param id                markup id.
     * @param tagCategoryIModel category model.
     * @param entityModel       tagable entity model.
     */
    public TagCategoryPanel(String id, IModel<TagCategory> tagCategoryIModel,
                            final IModel<? extends ITagable> entityModel) {
        super(id, tagCategoryIModel);

        genericManager.initialize(getModelObject(), getModelObject().getTags());

        add(new Label("name", getModelObject().getName()) {
            @Override
            public boolean isVisible() {
                return !(tagManager.getTagList(TagCategoryPanel.this.getModelObject(), false, false).isEmpty());
            }
        });
        add(new ListView<Tag>("tagList", new LoadableDetachableModel<List<? extends Tag>>() {
            @Override
            protected List<? extends Tag> load() {
                return new ArrayList<>(TagCategoryPanel.this.getModelObject().getTags());
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Tag> item) {
                boolean isChecked = tagEntityManager.checkTagPresent(
                        entityModel.getObject().getId(), item.getModelObject().getId());

                item.add(new TagPanel("tagPanel", item.getModel(), isChecked) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> valueModel) {
                        TagCategoryPanel.this.onUpdate(target, item.getModel(), valueModel);
                    }
                });
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_TAGS);
    }

    protected abstract void onUpdate(AjaxRequestTarget target, IModel<Tag> tagModel, IModel<Boolean> valueModel);
}
