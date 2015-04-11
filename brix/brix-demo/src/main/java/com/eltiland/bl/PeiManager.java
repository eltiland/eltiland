package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.PeiException;
import com.eltiland.model.Pei;
import com.eltiland.model.PostalAddress;
import com.eltiland.model.file.File;

import java.util.Collection;
import java.util.List;

/**
 * Pei Manager, containing methods related to PEI.
 *
 * @author Aleksey Plotnikov
 */
public interface PeiManager {
    /**
     * Creates and persists new PEI.
     *
     * @param pei pei to create.
     * @return new PEI
     */
    Pei createPei(Pei pei) throws PeiException;

    /**
     * updates PEI.
     *
     * @param pei PEI to create.
     * @return updated PEI
     */
    Pei updatePei(Pei pei) throws PeiException;

    /**
     * Delete item from DB.
     *
     * @param toDelete PEI to delete
     * @throws EltilandManagerException if item cannot be deleted
     */
    void deletePei(Pei toDelete) throws PeiException;

    /**
     * Get all PEIs.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of all PEIs
     */
    List<Pei> getPeiList(int index, Integer count, String sProperty, boolean isAscending, String searchString);

    /**
     * @return all PEIs count
     */
    int getPeiListCount(String searchString);
}