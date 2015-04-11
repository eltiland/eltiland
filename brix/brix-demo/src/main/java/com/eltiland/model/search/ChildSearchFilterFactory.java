package com.eltiland.model.search;

import com.eltiland.model.user.Child;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

/**
 * A complicated filter used to filter out the library search results.
 * <p/>
 * It takes a parameter when filtered, of type RecordSearchCriteria, and basing on that, filters out everything which is
 * irrelevant.
 */
public class ChildSearchFilterFactory {

    private Child.ChildSearchCriteria searchCriteria;

    private StandardAnalyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_36);

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

        if (searchCriteria.getPei() != null) {
            topQuery.add(new TermQuery(new Term("pei:id", Long.toString(searchCriteria.getPei().getId()))),
                    BooleanClause.Occur.MUST);
        }

        if (!searchCriteria.getExcludeChildren().isEmpty()) {
            BooleanQuery excludedChildrenSubQuery = new BooleanQuery();
            for (Child child : searchCriteria.getExcludeChildren()) {
                TermQuery termQuery = new TermQuery(new Term("id", Long.toString(child.getId())));
                excludedChildrenSubQuery.add(termQuery, BooleanClause.Occur.SHOULD);
            }
            topQuery.add(excludedChildrenSubQuery, BooleanClause.Occur.MUST_NOT);
        }

        return new QueryWrapperFilter(topQuery);
    }

    public Child.ChildSearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(Child.ChildSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
