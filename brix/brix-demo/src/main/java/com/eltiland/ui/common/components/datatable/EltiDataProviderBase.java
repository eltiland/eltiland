package com.eltiland.ui.common.components.datatable;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.hibernate.Hibernate;

import java.util.Iterator;

/**
 * Sortable data provider for poseidon data table. This data provider based on DefaultItemReuseStrategy,
 * when view is refreshed every request. For this reason, for wrapping item data object is used TransientReadOnlyModel.
 *
 * @param <T> The model object type
 * @author Ihor Cherednichenko
 * @version 1.0
 * @see com.eltiland.ui.common.model.TransientReadOnlyModel
 * @see org.apache.wicket.markup.repeater.DefaultItemReuseStrategy
 */
public abstract class EltiDataProviderBase<T extends Identifiable> extends SortableDataProvider<T> {
    /**
     * Create data provider. Default sorting properties: sort by id, ascending direction.
     */
    public EltiDataProviderBase() {
        Injector.get().inject(this);

        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Data iterator. Supply data for table in specified scope.
     *
     * @param first Start position of data scope
     * @param count Count elements is data scope
     * @return Condition survey data iterator
     */
    @SuppressWarnings({"unchecked"})
    public abstract Iterator iterator(int first, int count);

    /**
     * Total size of data elements. Used for paging.
     *
     * @return Total count of condition surveys.
     */
    public abstract int size();

    /**
     * Wrap data object into model. for data table item component construction. Used transient model.
     *
     * @param object Condition survey data object
     * @return Condition survey transient model
     * @see EltiDataProviderBase
     */
    @SuppressWarnings({"unchecked"})
    public IModel<T> model(T object) {
        return new GenericDBModel<>(Hibernate.getClass(object), object);
    }

    /**
     * Not used due DefaultItemReuseStrategy.
     *
     * @see EltiDataProviderBase
     */
    public void detach() {
    }
}
