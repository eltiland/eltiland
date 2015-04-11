package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqCategory;

import java.util.List;

/**
 * Manager for FAQ entity.
 *
 * @author Aleksey Plotnikov
 */
public interface FaqManager {

    /**
     * Finding faq list what have "text"
     *
     * @param category Faq category, where to search
     * @param text Text to find in faq
     * @return List of faqs
     */
    List<Faq> findByText(FaqCategory category, String text);

    /**
     * Creates and persist new FAQ entity.
     *
     * @param toCreate item to persist
     * @return persisted item.
     */
    Faq create(Faq toCreate) throws FaqException;

    /**
     * Get QA list, sorted by number.
     *
     * @return QA list, sorted by number.
     */
    List<Faq> getFaqList(FaqCategory category);

    /**
     * @return total count of QA's.
     */
    int getFaqCount(FaqCategory category, String searchString);

    /**
     * Get all QA's.
     *
     * @param index    the start position of the first result, numbered from 0.
     * @param count    the maximum number of results to retrieve. {@code null} means no limit.
     * @return List of all Paid Service Invoices.
     */
    List<Faq> getFaqList(FaqCategory category, int index, Integer count, String searchString);

    /**
     * Move QA up to one element.
     */
    void moveUp(Faq faq) throws FaqException;

    /**
     * Move QA down to one element.
     */
    void moveDown(Faq faq) throws FaqException;

    /**
     * Removing QA item.
     */
    void delete(Faq faq) throws FaqException;

    /**
     * Updating QA item.
     */
    void update(Faq faq) throws FaqException;
}
