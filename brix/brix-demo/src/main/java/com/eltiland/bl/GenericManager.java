package com.eltiland.bl;

import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Identifiable;
import org.hibernate.criterion.Criterion;

import java.util.Collection;
import java.util.List;

/**
 * A helper interface for constructing wicket's specific LoadableDetachableModel.
 * Technically, this is a typed access bean to the {@link org.hibernate.Session#get(Class, java.io.Serializable)}.
 * <p/>
 * As our implementation of such a model always requires an ID (Long in this case), we require all the entities which
 * want to be accessible in this way to be {@link Identifiable}.
 * <p/>
 * Please see GenericDBModel
 */
public interface GenericManager {
    /**
     * Load given {@link Identifiable} entity by ID.
     *
     * @param clazz Class of the entity to load.
     * @param id    Long identifier in the DB.
     * @param <T>   type parameter - classname of the entity to be loaded. We need this passed as well as class due to
     *              java's generic erasure
     * @return loaded entity.
     */
    <T extends Identifiable> T getObject(Class<T> clazz, Long id);

    /**
     * Load given {@link Identifiable} entitys by list of ID's.
     *
     * @param clazz Class of the entities to load.
     * @param ids   List of the identifiers in the DB.
     * @param <T>   type parameter - classname of the entity to be loaded. We need this passed as well as class due to
     *              java's generic erasure
     * @return loaded entities.
     */
    <T extends Identifiable> List<T> getObjects(Class<T> clazz, Collection<Long> ids);

    /**
     * Initialise given object.
     *
     * @param entity the existing entity with non initialized properties or collections.
     * @param proxy  the proxy of property or collection.
     */
    void initialize(Object entity, Object proxy);

    /**
     * Return count of the given entities having the exact search query.
     *
     * @param entityClass Type of the entity to get count for.
     * @param searchQuery search expression
     * @return count of the entities.
     */
    int getEntityCount(Class entityClass, String searchPropertyName, String searchQuery);

    /**
     * Returns list of generic entities filtered by given params. Can be used for paging, or, alternatively, may return
     * list of all existing entities if providing Null as first or count variables.
     *
     * @param entityClass        class of the entity to look for
     * @param searchPropertyName name of the search property
     * @param searchQuery        query for the search (entered by user into searchbox)
     * @param orderBy            order by
     * @param isAscending        whether search is ascending
     * @param first              offset
     * @param count              limit
     * @param <T>                type of the entity
     * @return list of the given entities.
     */
    <T extends Identifiable> List<T> getEntityList(Class<T> entityClass, String searchPropertyName, String searchQuery,
                                                   String orderBy, Boolean isAscending, Integer first, Integer count);

    <T extends Identifiable> List<T> getEntityList(Class<T> entityClass, List<String> joins,
                                                   List<Criterion> restrictions, String searchPropertyName,
                                                   String searchQuery, String orderBy, Boolean isAscending,
                                                   Integer first, Integer count);

    <T extends Identifiable> List<T> getEntityList(Class<T> entityClass, String orderBy);

    /**
     * Save new object of the given type.
     *
     * @param newObject the new object to be persisted to the db.
     */
    <T extends Identifiable> T saveNew(T newObject) throws ConstraintException;

    /**
     * Updates object of the given type.
     *
     * @param objectEdited object to update database with.
     */
    <T extends Identifiable> T update(T objectEdited) throws ConstraintException;

    /**
     * @param entity entity to refresh
     * @see org.hibernate.Session#refresh(Object).
     */
    void refresh(Object entity);

    /**
     * Try to delete entity. If operation is not possible, throw exception.
     *
     * @param entity entity to delete
     * @throws EltilandManagerException delete operation exception
     */
    void delete(Object entity) throws EltilandManagerException;
}
