package com.eltiland.bl.library;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.model.library.LibraryRecord;

import java.util.List;

/**
 * Library Collection entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface LibraryCollectionManager {
    /**
     * Remove collection (NOT subcollection!).
     */
    void removeCollection(LibraryCollection collection) throws EltilandManagerException;

    /**
     * Creates and persists new library collection entity.
     *
     * @param collection collection to create.
     * @return persisted record.
     */
    LibraryCollection createCollection(LibraryCollection collection) throws EltilandManagerException;

    /**
     * @return top level collection list, sorted by name.
     */
    List<LibraryCollection> getTopLibraryCollectionList();

    /**
     *
     * @param searchString search string.
     * @return top level collection count.
     */
    int getLibraryCollectionCount(String searchString, LibraryCollection parent, boolean isTopLevel);

    /**
     * Return collections list.
     *
     *
     * @param index        start index.
     * @param maxCount     entity max count.
     * @param sortProperty sorting property.
     * @param isAsc        ascending/descending sorting flag of sorting.
     * @param searchString search string.
     * @param parent
     * @return categories list.
     */
    List<LibraryCollection> getLibraryCollectionList(
            int index, int maxCount, String sortProperty, boolean isAsc, String searchString, LibraryCollection parent);

    /**
     * Adding record to collection.
     *
     * @param record     record to add to the collection.
     * @param collection collection to which record will be added.
     */
    void addRecordToCollection(LibraryRecord record, LibraryCollection collection) throws EltilandManagerException;
}
