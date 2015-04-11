package com.eltiland.model.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;

/**
 * Pei approved filter - filters out the PEIs which have not been approved yet.
 */
public class PeiApprovedFilter extends Filter {
    @Override
    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        OpenBitSet bitSet = new OpenBitSet(reader.maxDoc());
        TermDocs termDocs = reader.termDocs(new Term("approved", "true"));
        while (termDocs.next()) {
            bitSet.set(termDocs.doc());
        }
        return bitSet;
    }
}
