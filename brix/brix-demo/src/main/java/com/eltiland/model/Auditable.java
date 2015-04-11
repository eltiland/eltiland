package com.eltiland.model;

import com.eltiland.model.user.User;

import java.util.Date;

/**
* Important interface controlling audit of the DB work.
* <p/>
* Generally, requirement is that all the entities should keep track of who and when was creating and modifying them.
* In the application, this is achieved with 2 facilities:
* <ul>
* <li>Unit test checking that all the entities (except specifically stated exceptions) are auditable</li>
* <li>Aspect or Hibernate interceptor specifically working with these audit fields. Interceptor is using this interface
* as a marker telling it to write audit information to it. Audit info itself is taken from the Spring Security context
* and database (time). </li>
* </ul>
* <p/>
* Each entity may provide it under different mappings etc, so this is only an interface, not an abstract class.
* <p/>
* All the time values should come from TimeService rather than from new Date() to show up the tx time, not the current
* application server time.
*/
public interface Auditable {
    /**
     * Gets the creator of the given entity.
     *
     * @return user creating this entity.
     */
    User getCreator();

    /**
     * Sets the creator.
     *
     * @param creator user who is creating this entity.
     */
    void setCreator(User creator);

    /**
     * Gets creation date of the entity.
     *
     * @return date of creation of the entity.
     */
    Date getCreationDate();

    /**
     * Sets the creation date of the entity.
     *
     * @param creationDate creation date to set.
     */
    void setCreationDate(Date creationDate);
}

