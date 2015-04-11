package com.eltiland.bl.test;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestResult;

import java.util.List;

/**
 * Test Jump and Test Jump order entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface TestJumpManager {

    /**
     * Creates and persists new jump entity.
     *
     * @param jump jump to create.
     */
    TestJump createJump(TestJump jump) throws EltilandManagerException;

    /**
     * Updates jump entity.
     *
     * @param jump jump to update.
     */
    TestJump updateJump(TestJump jump) throws EltilandManagerException;

    /**
     * Deletes jump entity.
     *
     * @param jump jump to delete.
     */
    void deleteJump(TestJump jump) throws EltilandManagerException;

    /**
     * @return list of jumps for given result, sorted by order.
     */
    List<TestJump> getSortedJumps(TestResult result);

    /**
     * Moves up to 1 position jump element.
     *
     * @param jump jump to move.
     */
    void moveUp(TestJump jump) throws EltilandManagerException;

    /**
     * Moves down to 1 position jump element.
     *
     * @param jump jump to move.
     */
    void moveDown(TestJump jump) throws EltilandManagerException;

    /**
     * Get jump by it's order position.
     *
     * @param position order position.
     * @param result   result-parent.
     * @return jump by position.
     */
    TestJump getJumpByPosition(int position, TestResult result);
}
