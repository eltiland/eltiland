package com.eltiland.model.library;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.file.File;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.search.LibraryRecordSearchFactory;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.user.User;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Library record entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "record", schema = "library")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_prototype", discriminatorType = DiscriminatorType.STRING)
@FullTextFilterDefs({
        @FullTextFilterDef(name = "libraryRecordSearchFactory", impl = LibraryRecordSearchFactory.class)
})
public class LibraryRecord extends AbstractIdentifiable implements ITagable, Serializable {
    private String name;
    private String description;
    private String keyWords;
    private int relevance;
    private Date addDate;
    private Date publishedDate;
    private User publisher;
    private GoogleDriveFile content;
    private boolean published;
    private File fileContent;
    private boolean publishing;
    private int publishAttempts;

    private Set<LibraryCollection> collections = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 256)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 2048)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "relevance", nullable = false, columnDefinition = "numeric default 0")
    @Field
    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    @Column(name = "add_date", nullable = false)
    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "records")
    public Set<LibraryCollection> getCollections() {
        return collections;
    }

    public void setCollections(Set<LibraryCollection> collections) {
        this.collections = collections;
    }

    @Column(name = "published_date")
    @Field
    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher")
    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    @Override
    @Transient
    public String getTabName() {
        return "Записи библиотеки";
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content")
    public GoogleDriveFile getContent() {
        return content;
    }

    public void setContent(GoogleDriveFile content) {
        this.content = content;
    }

    @Column(name = "published", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_content")
    public File getFileContent() {
        return fileContent;
    }

    public void setFileContent(File fileContent) {
        this.fileContent = fileContent;
    }

    @Column(name = "publishing", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPublishing() {
        return publishing;
    }

    public void setPublishing(boolean publishing) {
        this.publishing = publishing;
    }

    @Column(name = "publish_attempts", nullable = false, columnDefinition = "numeric default 0")
    public int getPublishAttempts() {
        return publishAttempts;
    }

    public void setPublishAttempts(int publishAttempts) {
        this.publishAttempts = publishAttempts;
    }

    @Column(name = "key_words", length = 2048)
    @Field
    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    /**
     * Synthetic class for represent webinar user search criteria.
     */
    public static class LibrarySearchCriteria {
        private String searchQuery;
        private List<Tag> tagList;
        private LibraryCollection collection;

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LibraryRecord.LibrarySearchCriteria that = (LibraryRecord.LibrarySearchCriteria) o;

            if (collection != null ? !collection.equals(that.collection) : that.collection != null) return false;
            if (searchQuery != null ? !searchQuery.equals(that.searchQuery) : that.searchQuery != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = searchQuery != null ? searchQuery.hashCode() : 0;
            result = 31 * result + (collection != null ? collection.hashCode() : 0);
            return result;
        }

        public List<Tag> getTagList() {
            return tagList;
        }

        public void setTagList(List<Tag> tagList) {
            this.tagList = tagList;
        }

        public LibraryCollection getCollection() {
            return collection;
        }

        public void setCollection(LibraryCollection collection) {
            this.collection = collection;
        }
    }
}
