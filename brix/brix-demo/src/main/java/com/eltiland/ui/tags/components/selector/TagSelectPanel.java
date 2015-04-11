package com.eltiland.ui.tags.components.selector;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.model.tags.TagEntity;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Abstract tag management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class TagSelectPanel extends ELTDialogPanel implements IDialogConfirmCallback {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TagSelectPanel.class);

    @SpringBean
    private TagCategoryManager tagCategoryManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TagEntityManager tagEntityManager;

    private IDialogActionProcessor callback;

    private Map<Tag, Boolean> tagMap = new HashMap<>();

    private IModel<? extends ITagable> entityModel = new GenericDBModel<>(ITagable.class);

    private ListView<TagCategory> categoryList = new ListView<TagCategory>("categoryList") {
        @Override
        protected void populateItem(ListItem<TagCategory> components) {
            components.add(new TagCategoryPanel("categoryPanel", components.getModel(), entityModel) {
                @Override
                protected void onUpdate(AjaxRequestTarget target, IModel<Tag> tagModel, IModel<Boolean> valueModel) {
                    tagMap.put(tagModel.getObject(), valueModel.getObject());
                }
            });
        }
    };

    public TagSelectPanel(String id) {
        super(id);

        form.add(categoryList);
    }

    public void initPanel(IModel<? extends ITagable> entity) {
        entityModel = entity;

        List<TagCategory> categories = tagCategoryManager.getCategoryList(
                getEntityClass(entity.getObject()).getSimpleName(), false, false);

        categoryList.setList(categories);
        for (TagCategory category : categories) {
            genericManager.initialize(category, category.getTags());
            for (Tag tag : category.getTags()) {
                tagMap.put(tag, tagEntityManager.checkTagPresent(entityModel.getObject().getId(), tag.getId()));
            }
        }
    }


    @Override
    protected String getHeader() {
        return getString("tag_title");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            for (Map.Entry<Tag, Boolean> entry : tagMap.entrySet()) {
                boolean present = tagEntityManager.checkTagPresent(
                        entityModel.getObject().getId(), entry.getKey().getId());
                if (entry.getValue() && !present) {
                    TagEntity entity = new TagEntity();
                    entity.setTag(entry.getKey().getId());
                    entity.setEntity(entityModel.getObject().getId());
                    try {
                        genericManager.saveNew(entity);
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot add new tag", e);
                        throw new WicketRuntimeException(e);
                    }
                }
                if (!(entry.getValue()) && present) {
                    try {
                        tagEntityManager.deleteTag(entityModel.getObject().getId(), entry.getKey().getId());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot remove tag", e);
                        throw new WicketRuntimeException(e);
                    }
                }
                callback.process(target);
            }
        }
    }

    @Override
    public void setConfirmCallback(IDialogActionProcessor callback) {
        this.callback = callback;
    }

    protected Class<? extends ITagable> getEntityClass(ITagable entity) {
        return entity.getClass();
    }
}
