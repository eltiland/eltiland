package com.eltiland.model.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

/**
 * Pei approved filter - filters out the PEIs which have not been approved yet.
 */
public class PeiApprovedFilterFactory {
    private StandardAnalyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_36);

    private String approvedPropertyName = "approved";

    @Key
    public FilterKey getFilterKey() {
        StandardFilterKey filterKey = new StandardFilterKey();
        filterKey.addParameter(approvedPropertyName);

        return filterKey;
    }

    @Factory
    public Filter createFilter() throws ParseException {
        BooleanQuery topQuery = new BooleanQuery();
        return new QueryWrapperFilter(new TermQuery(new Term(approvedPropertyName, "true")));
    }

    public String getApprovedPropertyName() {
        return approvedPropertyName;
    }

    public void setApprovedPropertyName(String approvedPropertyName) {
        this.approvedPropertyName = approvedPropertyName;
    }
}
