package com.eltiland.bl.library;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.tags.Tag;

import java.util.List;

/**
 * Library Record entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface LibraryRecordManager {
    /**
     * Creates and persists new library record entity.
     *
     * @param record record to create.
     * @return persisted record.
     */
    LibraryRecord createRecord(LibraryRecord record) throws EltilandManagerException;

    /**
     * Removing library record entity.
     *
     * @param record record to remove.
     */
    void deleteRecord(LibraryRecord record) throws EltilandManagerException;

    /**
     * Updates existed record.
     *
     * @param record record to update.
     * @return update record.
     */
    LibraryRecord saveRecord(LibraryRecord record) throws EltilandManagerException;

    /**
     * Get record list with search params.
     *
     * @param searchString search string.
     * @param clazz        record class.
     * @param tags         tag list
     * @param collection
     * @param index        start index.
     * @param count        max results.
     * @param sProperty    sortProperty.
     * @param isAsc        asc/desc parameter.     @return record list.
     */
    List<LibraryRecord> getRecordList(String searchString, Class<? extends LibraryRecord> clazz,
                                      List<Tag> tags, LibraryCollection collection,
                                      int index, int count, String sProperty, boolean isAsc) throws EltilandManagerException;

    /**
     * Get record list count with search params.
     *
     * @param searchString search string.
     * @param clazz        record class.
     * @param tagList      tag list
     * @param collection
     * @return record list count.
     */
    int getRecordListCount(String searchString, Class<? extends LibraryRecord> clazz,
                           List<Tag> tagList, LibraryCollection collection) throws EltilandManagerException;

    /**
     * Get count of google records, which are not published.
     *
     * @return record count.
     */
    int getNotPublishedRecordCount();

    /**
     * @return first not published google record.
     */
    LibraryRecord getNotPublishedFirstRecord();

    /**
     * Get count of google records, which are not published.
     *
     * @param searchString search string.
     * @return record count.
     */
    int getNotConfirmedRecordCount(String searchString);

    /**
     * Get record list with search params.
     *
     * @param index        start index.
     * @param count        max results.
     * @param sProperty    sortProperty.
     * @param isAsc        asc/desc parameter.
     * @param searchString search string.
     * @return record list.
     */
    List<LibraryRecord> getNotConfirmedList(int index, int count, String sProperty, boolean isAsc, String searchString);
}
