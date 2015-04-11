package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;

/**
 * Manager for Property entity.
 *
 * @author Aleksey Plotnikov
 */
public interface PropertyManager {
    /**
     * Return property value by its key.
     *
     * @param key property key.
     * @return property value.
     */
    String getProperty(String key);

    /**
     * Saves property entity.
     *
     * @param key   property key.
     * @param value value.
     */
    void saveProperty(String key, String value) throws EltilandManagerException;
}
