package com.eltiland.model.tags;

import com.eltiland.model.Identifiable;

import java.util.Set;

/**
 * Interface for all entities that implementing search and filtering by tags.
 *
 * @author Aleksey Plotnikov.
 */
public interface ITagable extends Identifiable {
    /**
     * @return tab name for entity in admin panel.
     */
    String getTabName();
}
