package com.eltiland.bl.impl.library;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.model.library.LibraryDocumentRecord;
import com.eltiland.model.library.LibraryPresentationRecord;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagEntity;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Library record manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class LibraryRecordManagerImpl extends ManagerImpl implements LibraryRecordManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryRecordManager.class);

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private GoogleDriveManager googleDriveManager;
    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    @Override
    @Transactional
    public LibraryRecord createRecord(LibraryRecord record) throws EltilandManagerException {
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        record.setAddDate(DateUtils.getCurrentDate());
        record.setPublisher(currentUser);

        if (record.getFileContent() != null) {
            try {
                fileManager.saveFile(record.getFileContent());
            } catch (FileException e) {
                throw new EltilandManagerException(e.getMessage(), e);
            }
        }

        try {
            return genericManager.saveNew(record);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constrint exception while creating record", e);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(LibraryRecord record) throws EltilandManagerException {
        genericManager.initialize(record, record.getFileContent());
        genericManager.initialize(record, record.getCollections());
        if (record.getFileContent() != null) {
            File file = record.getFileContent();
            record.setFileContent(null);
            try {
                genericManager.update(record);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint exception while updating record", e);
            }
            try {
                fileManager.deleteFile(file);
            } catch (FileException e) {
                throw new EltilandManagerException(e.getMessage(), e);
            }
        }
        if (!record.getCollections().isEmpty()) {
            for (LibraryCollection collection : record.getCollections()) {
                genericManager.initialize(collection, collection.getRecords());
                collection.getRecords().remove(record);
                try {
                    genericManager.update(collection);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint exception while updating collection", e);
                }
            }
        }
        if (record.getContent() != null) {
            GoogleDriveFile file = record.getContent();
            record.setContent(null);
            try {
                genericManager.update(record);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint exception while updating record", e);
            }
//            try {
//               googleDriveManager.deleteFile(file);
//            } catch (GoogleDriveException e) {
//                throw new EltilandManagerException("GOOGLE error while removing file", e);
//            }
            genericManager.delete(file);
        }

        genericManager.delete(record);
    }

    @Override
    @Transactional
    public LibraryRecord saveRecord(LibraryRecord record) throws EltilandManagerException {
        try {
            genericManager.initialize(record, record.getFileContent());
            if ((record.getFileContent() != null) && (record.getFileContent().getId() == null)) {
                try {
                    fileManager.saveFile(record.getFileContent());
                } catch (FileException e) {
                    throw new EltilandManagerException(e.getMessage(), e);
                }
            }
            return genericManager.update(record);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constrint exception while updating record", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryRecord> getRecordList(String searchString, Class<? extends LibraryRecord> clazz,
                                             List<Tag> tags, LibraryCollection collection,
                                             int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getRecordCriteria(searchString, clazz, tags, collection);
        if (criteria == null) {
            return new ArrayList<>();
        }

        if (sProperty == null) {
            sProperty = "publishedDate";
        }
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getRecordListCount(String searchString, Class<? extends LibraryRecord> clazz,
                                  List<Tag> tagList, LibraryCollection collection) {
        Criteria criteria = getRecordCriteria(searchString, clazz, tagList, collection);
        return (criteria == null) ? 0 : criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public int getNotPublishedRecordCount() {
        Criteria documentCriteria = getCurrentSession().createCriteria(LibraryDocumentRecord.class);
        Criteria presentationCriteria = getCurrentSession().createCriteria(LibraryPresentationRecord.class);
        documentCriteria.add(Restrictions.eq("published", false));
        documentCriteria.add(Restrictions.eq("publishing", false));
        documentCriteria.add(Restrictions.lt("publishAttempts", 3));
        documentCriteria.add(Restrictions.isNotNull("publishedDate"));
        presentationCriteria.add(Restrictions.eq("published", false));
        presentationCriteria.add(Restrictions.eq("publishing", false));
        presentationCriteria.add(Restrictions.lt("publishAttempts", 3));
        presentationCriteria.add(Restrictions.isNotNull("publishedDate"));
        return presentationCriteria.list().size() + documentCriteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryRecord getNotPublishedFirstRecord() {
        Criteria documentCriteria = getCurrentSession().createCriteria(LibraryDocumentRecord.class);
        Criteria presentationCriteria = getCurrentSession().createCriteria(LibraryPresentationRecord.class);
        documentCriteria.add(Restrictions.eq("published", false));
        documentCriteria.add(Restrictions.eq("publishing", false));
        documentCriteria.add(Restrictions.lt("publishAttempts", 3));
        documentCriteria.add(Restrictions.isNotNull("publishedDate"));
        presentationCriteria.add(Restrictions.eq("published", false));
        presentationCriteria.add(Restrictions.eq("publishing", false));
        presentationCriteria.add(Restrictions.lt("publishAttempts", 3));
        presentationCriteria.add(Restrictions.isNotNull("publishedDate"));
        documentCriteria.setMaxResults(1);
        presentationCriteria.setMaxResults(1);
        List<LibraryDocumentRecord> records = documentCriteria.list();
        if (!records.isEmpty()) {
            return records.get(0);
        } else {
            List<LibraryPresentationRecord> pRecords = presentationCriteria.list();
            return pRecords.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getNotConfirmedRecordCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(LibraryRecord.class);
        if (searchString != null) {
            criteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.add(Restrictions.isNull("publishedDate"));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryRecord> getNotConfirmedList(
            int index, int count, String sProperty, boolean isAsc, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(LibraryRecord.class);
        if (searchString != null) {
            criteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.add(Restrictions.isNull("publishedDate"));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Transactional(readOnly = true)
    private Criteria getRecordCriteria(String searchString, Class<? extends LibraryRecord> clazz,
                                       List<Tag> tagList, LibraryCollection collection) {
        // Tags subsearch
        List<Long> entityIds = new ArrayList<>();
        if (tagList != null && !tagList.isEmpty()) {
            List<Long> tagIds = new ArrayList<>();
            for (Tag tag : tagList) {
                tagIds.add(tag.getId());
            }

            Criteria tagCriteria = getCurrentSession().createCriteria(TagEntity.class);
            tagCriteria.add(Restrictions.in("tag", tagIds));
            tagCriteria.setProjection(Projections.property("entity"));
            entityIds = tagCriteria.list();
            if (entityIds.isEmpty()) {
                return null;
            }
        }

        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.isNotNull("publishedDate"));

        if (searchString != null) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("keyWords", searchString, MatchMode.ANYWHERE).ignoreCase());
            criteria.add(searchCriteria);
        }

        if (tagList != null && !tagList.isEmpty()) {
            criteria.add(Restrictions.in("id", entityIds));
        }

        if (collection != null) {
            criteria.createAlias("collections", "collections", JoinType.LEFT_OUTER_JOIN);
            criteria.add(Restrictions.eq("collections.id", collection.getId()));
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }

        return criteria;
    }
}
