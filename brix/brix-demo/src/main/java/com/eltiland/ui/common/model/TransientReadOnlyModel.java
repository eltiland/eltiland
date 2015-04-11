package com.eltiland.ui.common.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.lang.Objects;

/**
 * Simple transient model for wrapper detachable readonly object.
 * Used for data provider for data table components.
 *
 * @param <T> The model object
 * @author Ihor Cherednichenko
 * @version 1.0
 */
public class TransientReadOnlyModel<T> extends AbstractReadOnlyModel<T> {
    private transient T object;

    /**
     * During model construction object stored in model in transient state.
     *
     * @param object Model object
     */
    public TransientReadOnlyModel(T object) {
        this.object = object;
    }

    @Override
    public T getObject() {
        if (object == null) {
            throw new NullPointerException("TransientReadOnlyModel is unusable after deserialization!");
        }

        return object;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(object);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransientReadOnlyModel<?>)) {
            return false;
        }
        TransientReadOnlyModel<?> that = (TransientReadOnlyModel<?>) obj;
        return Objects.equal(object, that.object);
    }
}
