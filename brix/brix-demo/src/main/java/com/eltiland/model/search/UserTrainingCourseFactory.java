package com.eltiland.model.search;

import com.eltiland.model.user.User;
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
 * A complicated filter used to filter the webinar users by given webinar.
 */
public class UserTrainingCourseFactory {

    private User.UserTrainingCourseCriteria searchCriteria;

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

        if (searchCriteria.getIds() != null && !(searchCriteria.getIds().isEmpty())) {
            for (Long id : searchCriteria.getIds()) {
                topQuery.add(new TermQuery(new Term("id", Long.toString(id))), BooleanClause.Occur.MUST_NOT);
            }
        }

        return new QueryWrapperFilter(topQuery);
    }

    public void setSearchCriteria(User.UserTrainingCourseCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public StandardAnalyzer getStandardAnalyzer() {
        return standardAnalyzer;
    }
}
