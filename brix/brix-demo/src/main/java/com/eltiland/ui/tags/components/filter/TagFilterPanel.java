package com.eltiland.ui.tags.components.filter;

import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Tag filter panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TagFilterPanel extends BaseEltilandPanel {

    @SpringBean
    private TagCategoryManager tagCategoryManager;
    @SpringBean
    private TagManager tagManager;

    private boolean isPanelShown;

    private IModel<List<Tag>> tagSelectedList = new GenericDBListModel<>(Tag.class);

    private WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");
    private WebMarkupContainer showLink = new WebMarkupContainer("showLink");
    private WebMarkupContainer mainContainer = new WebMarkupContainer("mainContainer");

    private Label linkLabel = new Label("linkTitle", new Model<String>());

    public TagFilterPanel(String id, Class<? extends ITagable> clazz, boolean isShown) {
        super(id);

        add(mainContainer);
        mainContainer.setVisible(tagManager.isEntityHasAnyTag(clazz.getSimpleName()));

        isPanelShown = isShown;
        mainContainer.add(showLink);

        showLink.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                isPanelShown = !isPanelShown;

                target.appendJavaScript(getShowDataContainerScript(isPanelShown, true));
                linkLabel.setDefaultModelObject(getString(isPanelShown ? "hide" : "show"));
                target.add(linkLabel);
                onChangeShown(target, isPanelShown);
            }
        });

        mainContainer.add(dataContainer.setOutputMarkupPlaceholderTag(true));
        showLink.add(linkLabel);
        linkLabel.setDefaultModelObject(getString(isPanelShown ? "hide" : "show"));
        linkLabel.setOutputMarkupId(true);

        dataContainer.add(new ListView<TagCategory>(
                "categoryList", tagCategoryManager.getCategoryList(clazz.getSimpleName(), false, false)) {
            @Override
            protected void populateItem(ListItem<TagCategory> components) {
                components.add(new TagCategoryPanel("categoryPanel", components.getModel()) {
                    @Override
                    protected void onClick(AjaxRequestTarget target, Tag tag, boolean value) {
                        boolean present = tagSelectedList.getObject().contains(tag);

                        if (!value && present) {
                            tagSelectedList.getObject().remove(tag);
                        }
                        if (value && !present) {
                            tagSelectedList.getObject().add(tag);
                        }
                        TagFilterPanel.this.onClick(target, tagSelectedList);
                    }

                    @Override
                    protected boolean isTagSelected(Tag tag) {
                        return tagSelectedList.getObject().contains(tag);
                    }
                });
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript(getShowDataContainerScript(isPanelShown, false));
    }

    public void initTagList(IModel<List<Tag>> tagListModel) {
        tagSelectedList = tagListModel;
    }

    private String getShowDataContainerScript(boolean isShow, boolean isSlow) {
        String speed = isSlow ? "slow" : "hide";
        String action = isShow ? "show" : "hide";
        return ("$('#" + dataContainer.getMarkupId() + "')." + action + "('" + speed + "')");
    }

    protected abstract void onClick(AjaxRequestTarget target, IModel<List<Tag>> tagListModel);

    protected abstract void onChangeShown(AjaxRequestTarget target, boolean isShown);
}
