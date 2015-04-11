package com.eltiland.bl;

import com.eltiland.exceptions.CountableException;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.Countable;

import java.util.List;

/**
 * Manager for entities supporting index and moving up/down.
 *
 * @author Aleksey Plotnikov.
 */
public interface CountableManager<T extends AbstractIdentifiable & Countable> {

    /**
     * Create countable entity.
     */
    T create(T entity) throws CountableException;

    /**
     * Updates entity with current max number.
     */
    T enumerate(T entity) throws CountableException;

    /**
     * Delete countable entity.
     */
    void delete(T entity) throws CountableException;

    /**
     * Pseudo delete countable entity (assigning -1 as index).
     */
    void pseudoDelete(T entity) throws CountableException;

    /**
     * Get entity by it's index.
     */
    T getEntityByIndex(int index, Class<? extends AbstractIdentifiable> clazz);

    /**
     * Move entity up to 1 position.
     */
    void moveUp(T entity) throws CountableException;

    /**
     * Move entity down to 1 position.
     */
    void moveDown(T entity) throws CountableException;

    /**
     * Get entity count. All except entities with index -1 (removed).
     */
    int getEntityCount(Class<? extends AbstractIdentifiable> clazz);

    /**
     * Get entity list, except removed. sorted by index ascending.
     */
    List<T> getEntityList(Class<? extends AbstractIdentifiable> clazz, int first, int count);
}
