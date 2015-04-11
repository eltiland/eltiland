package com.eltiland.model.library;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * M-M record relation map table.
 */
@Entity
@Table(name = "record_collection", schema = "library")
public class RecordCollection extends AbstractIdentifiable {
    private LibraryRecord record;
    private LibraryCollection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    public LibraryRecord getRecord() {
        return record;
    }

    public void setRecord(LibraryRecord record) {
        this.record = record;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    public LibraryCollection getCollection() {
        return collection;
    }

    public void setCollection(LibraryCollection collection) {
        this.collection = collection;
    }
}
