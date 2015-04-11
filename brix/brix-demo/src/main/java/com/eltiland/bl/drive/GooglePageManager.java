package com.eltiland.bl.drive;

import com.eltiland.model.google.GooglePage;

import java.util.List;

/**
 * Google Page entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface GooglePageManager {

    /**
     * Returns page by it's name.
     *
     * @param name page name.
     * @return google page entity.
     */
    GooglePage getPageByName(String name);

    /**
     * Get google panel's count.
     *
     * @param searchString search String.
     * @return panel's count.
     */
    int getPagesCount(String searchString);

    /**
     * Get google panels.
     *
     * @param searchString search String.
     * @param first        the start position of the first result, numbered from 0.
     * @param count        the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty    the sorting property name
     * @param isAscending  the sorting direction.
     * @return panel's list
     */
    List<GooglePage> getPagesList(String searchString, int first, int count, String sProperty, boolean isAscending);
}
