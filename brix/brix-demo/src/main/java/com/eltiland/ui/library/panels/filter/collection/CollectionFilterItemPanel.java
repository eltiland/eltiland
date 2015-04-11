package com.eltiland.ui.library.panels.filter.collection;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.library.panels.filter.AbstractFilterItemPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Collection filter item panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CollectionFilterItemPanel extends AbstractFilterItemPanel<LibraryCollection> {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constrctor.
     *
     * @param id     markup id.
     * @param object filter object.
     * @param status status (checked/not)
     */
    public CollectionFilterItemPanel(String id, LibraryCollection object, final boolean status) {
        super(id, object, status);
        if (object.getDescription() != null && !(object.getDescription().isEmpty())) {
            add(new AttributeModifier("title", new Model<>(object.getDescription())));
            add(new TooltipBehavior());
        }
    }

    @Override
    protected IModel<String> getName(LibraryCollection object) {
        return new Model<>(object.getName());
    }

    @Override
    protected int getCount(LibraryCollection object) {
        genericManager.initialize(object, object.getRecords());
        genericManager.initialize(object, object.getSubCollections());
        int count = object.getRecords().size();
        for( LibraryCollection subCollection : object.getSubCollections()) {
            genericManager.initialize(subCollection, subCollection.getRecords());
            count += subCollection.getRecords().size();
        }

        return count;
    }
}
