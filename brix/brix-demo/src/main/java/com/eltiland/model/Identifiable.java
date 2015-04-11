package com.eltiland.model;

/**
 * Little interface resembling something having a Long ID in a simple form which can be used to load it from DB.
 * This interface is used in helpers connecting Wicket's LoadableDetachableModels to entities.
 * <p/>
 * Most of the entities of the Eltiland should be {@link Identifiable}.
 */
public interface Identifiable
{
    /**
     * Returns a Long id which identifies this object. It will be mostly used to load this object from DB.
     *
     * @return ID of the object.
     */
    Long getId();

}
