package com.eltiland.ui.library.panels.filter.tag;

import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.model.tags.Tag;
import com.eltiland.ui.library.panels.filter.AbstractFilterItemPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tag filter item panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TagFilterItemPanel extends AbstractFilterItemPanel<Tag> {

    @SpringBean
    private TagEntityManager tagEntityManager;

    /**
     * Panel constrctor.
     *
     * @param id markup id.
     */
    public TagFilterItemPanel(String id, Tag object, boolean status) {
        super(id, object, status);
    }

    @Override
    protected IModel<String> getName(Tag object) {
        return new Model<>(object.getName());
    }

    @Override
    protected int getCount(Tag object) {
        return tagEntityManager.getEntityIds(new ArrayList<>(Arrays.asList(object))).size();
    }
}
