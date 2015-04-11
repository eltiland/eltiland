package com.eltiland.model.search;

import com.eltiland.model.library.LibraryRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A complicated filter used to filter the webinar users by given webinar.
 */
public class LibraryRecordSearchFactory {

    private LibraryRecord.LibrarySearchCriteria searchCriteria;

    private StandardAnalyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_36);

    private IndexSearcher searcher;


    @Key
    public FilterKey getFilterKey() {
        StandardFilterKey filterKey = new StandardFilterKey();
        filterKey.addParameter(searchCriteria);

        return filterKey;
    }

    @Factory
    public Filter createFilter() throws ParseException {
        BooleanQuery topQuery = new BooleanQuery();
        topQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
//        try {
//            Directory directory = FSDirectory.open(new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\temp\\lucene\\indexes\\"));
//            IndexReader reader = IndexReader.open(directory);
//
//            searcher = new IndexSearcher(reader);
//            TopDocs hits = searcher.search(topQuery, 10);
//            ScoreDoc[] scoreDocs = hits.scoreDocs;
//
//            List<Document> luceneDocuments = new ArrayList<Document>();
//            for( int i = 0; i < 10; i++ ) {
//                luceneDocuments.add(searcher.doc(scoreDocs[i].doc));
//            }
//
//
//
//            return new Filter() {
//                @Override
//                public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
//                    return new DocIdSet() {
//                        @Override
//                        public DocIdSetIterator iterator() throws IOException {
//                            return new DocIdSetIterator() {
//
//                                @Override
//                                public int docID() {
//                                    return 0;  //To change body of implemented methods use File | Settings | File Templates.
//                                }
//
//                                @Override
//                                public int nextDoc() throws IOException {
//                                    return 0;  //To change body of implemented methods use File | Settings | File Templates.
//                                }
//
//                                @Override
//                                public int advance(int target) throws IOException {
//                                    return 0;  //To change body of implemented methods use File | Settings | File Templates.
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
        return new QueryWrapperFilter(topQuery);
    }

    public void setSearchCriteria(LibraryRecord.LibrarySearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public StandardAnalyzer getStandardAnalyzer() {
        return standardAnalyzer;
    }
}
