package com.eltiland.bl;

import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarEvent;

/**
 * Interface for managing Webinars Events.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarEventManager {

    /**
     * Webinar event creation. Create event on webinar service and persist webinar item.
     *
     * @param event webinar event structure to create.
     * @return newly persisted webinar entity.
     */
    WebinarEvent create(WebinarEvent event) throws WebinarException;
}
