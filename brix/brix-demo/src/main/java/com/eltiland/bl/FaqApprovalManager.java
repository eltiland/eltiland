package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.FaqApproval;

import java.util.List;

/**
 * Manager for FAQ Approval entity.
 *
 * @author Aleksey Plotnikov
 */
public interface FaqApprovalManager {

    boolean isExists(FaqApproval faqApproval);

    void update(FaqApproval item) throws FaqException;

    /**
     * Creates and persist new FAQ Approval entity.
     *
     * @param toCreate item to persist
     * @return persisted item.
     */
    FaqApproval create(FaqApproval toCreate) throws FaqException;

    /**
     * Removes FAQ Approval entity.
     *
     * @param toDelete item to delete
     */
    void delete(FaqApproval toDelete) throws FaqException;

    /**
     * @return count of FAQ on approval.
     */
    int getFaqApprovalCount(String searchString);

    /**
     * Get all FAQ on approval.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of all FAQ on approval.
     */
    List<FaqApproval> getFaqApprovalList(int index, Integer count, String sProperty, boolean isAscending,
                                         String searchString);
}
