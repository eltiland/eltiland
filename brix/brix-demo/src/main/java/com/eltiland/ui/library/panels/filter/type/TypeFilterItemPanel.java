package com.eltiland.ui.library.panels.filter.type;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.library.panels.filter.AbstractFilterItemPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Type filter panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TypeFilterItemPanel extends AbstractFilterItemPanel<Class<? extends LibraryRecord>> {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constrctor.
     *
     * @param id markup id.
     */
    public TypeFilterItemPanel(String id, Class<? extends LibraryRecord> object, boolean status) {
        super(id, object, status);
    }

    @Override
    protected IModel<String> getName(Class<? extends LibraryRecord> object) {
        return new Model<>(getString(object.getSimpleName() + ".type"));
    }

    @Override
    protected int getCount(Class<? extends LibraryRecord> object) {
        return genericManager.getEntityCount(object, null, null);
    }
}
