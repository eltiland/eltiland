package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Slider;

/**
 * Slider Manager.
 *
 * @author Aleksey Plotnikov
 */
public interface SliderManager {
    /**
     * Move up slider entity to one position.
     *
     * @param slider slider entity to move.
     */
    void moveUp(Slider slider) throws EltilandManagerException;

    /**
     * Move down slider entity to one position.
     *
     * @param slider slider entity to move.
     */
    void moveDown(Slider slider) throws EltilandManagerException;
}