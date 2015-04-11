package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.FaqCategory;

import java.util.List;

/**
    Faq category manager
    Author: Pavel Androschuk
 */
public interface FaqCategoryManager {
    FaqCategory create(FaqCategory item) throws FaqException;
    List<FaqCategory> getList();
    int getCount(String searchString);

    /**
     * Get all QA's categoryes.
     *
     * @param index    the start position of the first result, numbered from 0.
     * @param count    the maximum number of results to retrieve. {@code null} means no limit.
     * @return List of all Paid Service Invoices.
     */
    List<FaqCategory> getList(int index, Integer count, String searchString);
    FaqCategory getById(long id);
    void moveUp(FaqCategory item) throws FaqException;
    void moveDown(FaqCategory item) throws FaqException;
    void delete(FaqCategory item) throws FaqException;
    void update(FaqCategory item) throws FaqException;
}
