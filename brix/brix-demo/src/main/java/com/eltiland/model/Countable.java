package com.eltiland.model;

/**
 * Interface for entities, which have number and functions for it's changing.
 */
public interface Countable
{
    /**
     * Returns a index of the entity.
     */
    public Integer getIndex() ;

    /**
     * Set index of entity.
     */
    public void setIndex(Integer index);
}
