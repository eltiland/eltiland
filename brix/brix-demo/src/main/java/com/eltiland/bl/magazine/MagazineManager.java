package com.eltiland.bl.magazine;

import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.magazine.Magazine;

import java.util.List;

/**
 * Manager for Magazine entity.
 *
 * @author Aleksey Plotnikov
 */
public interface MagazineManager {

    /**
     * Creates and persists new Magazine.
     *
     * @param magazine magazine to create.
     * @return new created magazine.
     */
    Magazine createMagazine(Magazine magazine) throws EltilandManagerException, CountableException, FileException;

    /**
     * Updates Magazine.
     *
     * @param magazine magazine to update.
     * @return updated magazine.
     */
    Magazine updateMagazine(Magazine magazine) throws EltilandManagerException, FileException;

    /**
     * Deletes Magazine (set active to false and delete content and cover)
     *
     * @param magazine magazine to delete.
     */
    void deleteMagazine(Magazine magazine) throws FileException, CountableException;

    /**
     * @return count of all not-deleted magazines.
     */
    int getMagazineCount();

    /**
     * @return list of the active magazines.
     */
    List<Magazine> getListOfMagazines();

    /**
     * Get formatted list of magazines.
     *
     * @param index     first index.
     * @param count     count of results.
     * @param sProperty sorting property.
     * @param isAsc     sorting direction.
     * @return list of magazines.
     */
    List<Magazine> getListOfMagazines(int index, int count, String sProperty, boolean isAsc);
}
