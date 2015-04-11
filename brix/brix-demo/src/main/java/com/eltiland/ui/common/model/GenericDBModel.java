package com.eltiland.ui.common.model;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.Identifiable;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Objects;

/**
 * Generic {@link org.apache.wicket.model.LoadableDetachableModel} implementation.
 * Entities loadable in this way should be {@link com.eltiland.model.Identifiable}
 * to provide ID to the loader.
 * <p/>
 * As java generics utilize type erasure, we still need not only to specify concrete class as a type parameter but also
 * give it as a constructor param.
 *
 * @param <T> Identifiable type to load.
 */
public class GenericDBModel<T extends Identifiable> extends LoadableDetachableModel<T> {

    protected Long id = null;

    private Class<T> clazz;

    @SpringBean
    private GenericManager genericManager;

    /**
     * Constructor with an initialized object. In this state we will not load it once more.
     *
     * @param object the existing object.
     * @param clazz  class of the object to load. Should be the same as type parameter T.
     */
    public GenericDBModel(Class<T> clazz, T object) {
        this(clazz);
        setObject(object);
    }

    /**
     * Constructor with a not initialized object. In this state we will load when it will be required.
     *
     * @param clazz class of the object to load. Should be the same as type parameter T.
     * @param id    id of the model object.
     */
    public GenericDBModel(Class<T> clazz, Long id) {
        this(clazz);
        this.id = id;
    }

    /**
     * Default constructor (no-op).
     *
     * @param clazz class of the entities we'd like to load.
     */
    public GenericDBModel(Class<T> clazz) {
        this.clazz = clazz;
        Injector.get().inject(this);
    }


    @Override
    public void setObject(T object) {
        super.setObject(object);
        if (object != null) {
            id = object.getId();
        } else {
            id = null;
        }
    }

    @Override
    protected T load() {
        if (id == null) {
            return null;
        }
        return loadEntity(clazz, id);
    }

    /**
     * Loads entity object. Override to define your own load mechanism.
     * {@link GenericManager#getObject(Class, Long)} is used by default.
     *
     * @param clazz class
     * @param id    entity id
     * @return entity object
     */
    protected T loadEntity(Class<T> clazz, Long id) {
        return genericManager.getObject(clazz, id);
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
        if (!(obj instanceof GenericDBModel<?>)) {
            return false;
        }
        GenericDBModel<?> that = (GenericDBModel<?>) obj;
        return Objects.equal(getObject(), that.getObject());
    }
}
