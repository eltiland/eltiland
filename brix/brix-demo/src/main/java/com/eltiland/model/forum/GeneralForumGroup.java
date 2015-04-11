package com.eltiland.model.forum;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity for general forum group
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("GENERAL")
public class GeneralForumGroup extends ForumGroup {
}
