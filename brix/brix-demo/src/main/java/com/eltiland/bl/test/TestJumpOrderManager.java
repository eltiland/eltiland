package com.eltiland.bl.test;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestJumpOrder;

import java.util.List;

/**
 * Test Jump and Test Jump order entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface TestJumpOrderManager {

    /**
     * Creates and persists new jump order entity.
     *
     * @param order order to create.
     */
    TestJumpOrder createJumpOrder(TestJumpOrder order) throws EltilandManagerException;

    /**
     * Updates jump order entity.
     *
     * @param order order to update.
     */
    TestJumpOrder updateJumpOrder(TestJumpOrder order) throws EltilandManagerException;

    /**
     * Deletes jump order entity.
     *
     * @param order order to delete.
     */
    void deleteJumpOrder(TestJumpOrder order) throws EltilandManagerException;

    /**
     * Deletes all jump orders without jump.
     */
    void deleteAllOrphans() throws EltilandManagerException;

    /**
     * Get sorted jump orders items for given jump.
     *
     * @param jump jump for search.
     * @return sorted orders list.
     */
    List<TestJumpOrder> getSortedJumpOrders(TestJump jump);
}
