package com.eltiland.bl.impl.library;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.library.LibraryCollectionManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.library.RecordCollection;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Library collecction manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class LibraryCollectionManagerImpl extends ManagerImpl implements LibraryCollectionManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private IndexCreator indexCreator;

    @Override
    @Transactional
    public void removeCollection(LibraryCollection collection) throws EltilandManagerException {
        genericManager.initialize(collection, collection.getRecords());
        collection.getRecords().clear();
        try {
            genericManager.update(collection);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint exception when saving collection", e);
        }
        genericManager.delete(collection);
        indexCreator.doRebuildIndex(LibraryCollection.class);
    }

    @Override
    @Transactional
    public LibraryCollection createCollection(LibraryCollection collection) throws EltilandManagerException {
        try {
            LibraryCollection collection1 = genericManager.saveNew(collection);
            indexCreator.doRebuildIndex(LibraryCollection.class);
            return collection1;
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint exception while creating collection", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryCollection> getTopLibraryCollectionList() {
        Criteria criteria = getCurrentSession().createCriteria(LibraryCollection.class);
        criteria.add(Restrictions.isNull("parent"));
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }


    @Override
    @Transactional(readOnly = true)
    public int getLibraryCollectionCount(String searchString, LibraryCollection parent, boolean isTopLevel) {
        try {
            LibraryCollection.LibraryCollectionSearchCriteria criteria = createLibrarySearchCriteria(parent, isTopLevel);
            FullTextQuery hibQuery = createSearchFullTextQuery(searchString, criteria);

            return hibQuery.getResultSize();
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Error querying Library index", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryCollection> getLibraryCollectionList(
            int index, int maxCount, String sortProperty,
            boolean isAsc, String searchString, LibraryCollection parent) {
        try {
            LibraryCollection.LibraryCollectionSearchCriteria criteria = createLibrarySearchCriteria(parent, false);
            FullTextQuery hibQuery = createSearchFullTextQuery(searchString, criteria);
            hibQuery.setFirstResult(index);
            hibQuery.setMaxResults(maxCount);
            hibQuery.setSort(new Sort(new SortField("weight", SortField.FLOAT, isAsc)));
            return hibQuery.list();
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Error querying Library index", e);
        }
    }

    @Override
    @Transactional
    public void addRecordToCollection(LibraryRecord record, LibraryCollection collection)
            throws EltilandManagerException {
        RecordCollection rc = new RecordCollection();
        rc.setRecord(record);
        rc.setCollection(collection);
        try {
            genericManager.saveNew(rc);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Error while creating new link", e);
        }
        collection.getRecords().add(record);
        try {
            genericManager.update(collection);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Error while saving collection", e);
        }
    }


    private FullTextQuery createSearchFullTextQuery(String searchString,
                                                    LibraryCollection.LibraryCollectionSearchCriteria criteria)
            throws IOException, ParseException {
        //Create a multi-field Lucene query
        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());

        org.apache.lucene.search.Query query;

        RussianAnalyzer russianAnalyzer = new RussianAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_36, "name", russianAnalyzer);

        if (StringUtils.isEmpty(searchString)) {
            query = parser.parse("(*:*)");
        } else {
            query = parser.parse(QueryParser.escape(searchString));
        }

        FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, LibraryCollection.class);
        hibQuery.enableFullTextFilter("librarySearchFilterFactory").setParameter("searchCriteria", criteria);
        return hibQuery;
    }

    /**
     * Construct search criteria which will be used by Lucene search engine to find library collections.
     *
     * @param collection parent
     * @param topLevel   if TRUE - will search only top level collection.
     * @return criteria for search
     */
    private LibraryCollection.LibraryCollectionSearchCriteria createLibrarySearchCriteria(
            LibraryCollection collection, boolean topLevel) {
        LibraryCollection.LibraryCollectionSearchCriteria searchCriteria =
                new LibraryCollection.LibraryCollectionSearchCriteria();
        searchCriteria.setParent(collection);
        searchCriteria.setTopLevel(topLevel);
        return searchCriteria;
    }
}
