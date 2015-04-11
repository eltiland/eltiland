package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course paid invoice manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CoursePaidInvoiceManager {

    /**
     * Creates and persists new course paid invoice entity.
     *
     * @param coursePaidInvoice course paid invoice to create.
     * @return created course.
     */
    CoursePaidInvoice createCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice) throws EltilandManagerException;

    /**
     * Updates course paid invoice entity.
     *
     * @param coursePaidInvoice course paid invoice to update.
     * @return updated course.
     */
    CoursePaidInvoice updateCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice) throws EltilandManagerException;

    /**
     * Removes course paid invoice entity.
     *
     * @param coursePaidInvoice course paid invoice to remove.
     */
    void removeCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice) throws EltilandManagerException;

    /**
     * Fetch course paid invoice entity.
     *
     * @param coursePaidInvoice course paid invoice to fetch.
     * @return fethed entity.
     */
    CoursePaidInvoice fetchCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice);

    /**
     * Check if given course is paid.
     *
     * @param course course for checking.
     * @return TRUE, if course is paid.
     */
    boolean isCoursePaid(Course course);

    /**
     * Check if given item is paid.
     *
     * @param item item for checking.
     * @return TRUE, if item is paid.
     */
    boolean isBlockPaid(FolderCourseItem item);

    /**
     * Check if given course has not approved yet invoices.
     *
     * @param course course for checking.
     * @return TRUE, if course is paid.
     */
    boolean hasNotApprovedInvoice(Course course);

    /**
     * Check if given item has not approved yet invoices.
     *
     * @param item item for checking.
     * @return TRUE, if course is paid.
     */
    boolean hasNotApprovedInvoice(FolderCourseItem item);

    /**
     * @param index     index of the first element.
     * @param count     count of elements.
     * @param sProperty sorting property.
     * @param isAsc     if TRUE - use ascending sorting.
     * @return List of not approved paid invoices.
     */
    List<CoursePaidInvoice> getListOfNotApprovedInvoices(int index, int count, String sProperty, boolean isAsc);

    /**
     * @return count of the not approved invoices.
     */
    int getCountOfNotApprovedInvoices();

    /**
     * Get actual paid invoice for course and item.
     *
     * @param course paid course.
     * @param item   paid item of the course. If NULL - actual invoice will correspond to whole course.
     * @return actual paid invoice (NULL if entity not paid or invoice was canceled).
     */
    CoursePaidInvoice getActualInvoice(Course course, FolderCourseItem item);

    /**
     * @return TRUE if course is entire paid.
     */
    boolean isCourseEntirePaid(Course course, User user);

    /**
     * Return list of paid blocks for given course, which are not paid by given user
     *
     *
     * @param course course for test.
     * @param user user-listener
     * @return list of paid blocks.
     */
    List<FolderCourseItem> getPaidBlocksForCourse(Course course, User user);
}
