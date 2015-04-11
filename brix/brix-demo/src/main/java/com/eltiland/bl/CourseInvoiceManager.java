package com.eltiland.bl;

import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course invoice manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseInvoiceManager {
    /**
     * Check ability to access user to the course.
     *
     * @param course Course to check.
     * @param user   to check.
     * @return TRUE, if user has granted access to the course.
     */
    boolean checkAccessToCourse(Course course, User user);

    /**
     * Check if invoice (not applied) is present.
     *
     * @param course Course to check.
     * @param user   to check.
     * @return TRUE, if user already has not approved invoice.
     */
    boolean checkInvoicePresent(Course course, User user);

    /**
     * @return not approved invoices count.
     */
    int getInvoicesCount();

    /**
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of not approved courses.
     */
    List<CourseInvoice> getCourseInvoiceList(int index, Integer count, String sProperty, boolean isAscending);
}
