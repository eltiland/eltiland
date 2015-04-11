package com.eltiland.ui.common.model;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.Identifiable;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author knorr
 * @version 1.0
 * @since 8/7/12
 */
public class GenericDBListModel<T extends Identifiable> extends LoadableDetachableModel<List<T>> {

    protected List<Long> ids;

    protected Class<T> clazz;

    @SpringBean
    private GenericManager genericManager;

    /**
     * Constructor with pre initialized objects.
     *
     * @param clazz      class of the entities
     * @param objectList list of the objects
     */
    public GenericDBListModel(Class<T> clazz, List<T> objectList) {
        this(clazz);
        setObject(objectList);
    }

    /**
     * Default constructor (no-op).
     *
     * @param clazz class of the entities we'd like to load.
     */
    public GenericDBListModel(Class<T> clazz) {
        this.clazz = clazz;
        Injector.get().inject(this);
    }

    @Override
    public void setObject(List<T> objects) {
        super.setObject(objects);
        if (objects == null || objects.isEmpty()) {
            ids = null;
        } else {
            ids = new ArrayList<Long>(objects.size());
            for (T object : objects) {
                ids.add(object.getId());
            }
        }
    }

    @Override
    protected List<T> load() {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<T>();
        }
        // TODO: workaround for list with non unique objects. Optimize later!!
        List<T> uniqueObjects = genericManager.getObjects(clazz, ids);
        Map<Long, T> id2obj = new HashMap<>(uniqueObjects.size());
        for (T uniqueObject : uniqueObjects) {
            id2obj.put(uniqueObject.getId(), uniqueObject);
        }
        List<T> objects = new ArrayList<>(uniqueObjects.size());
        for (Long id : ids) {
            objects.add(id2obj.get(id));
        }
        return objects;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getObject());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenericDBListModel<?>)) {
            return false;
        }
        GenericDBListModel<?> that = (GenericDBListModel<?>) obj;
        return Objects.equal(getObject(), that.getObject());
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        ids = new ArrayList<Long>();

        if (getObject() == null) {
            return;
        }

        for (T object : getObject()) {
            ids.add(object.getId());
        }
    }
}