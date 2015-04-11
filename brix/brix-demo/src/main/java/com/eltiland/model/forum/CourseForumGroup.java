package com.eltiland.model.forum;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity for forum group, related to specified course.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("COURSE")
public class CourseForumGroup extends ForumGroup {
}
