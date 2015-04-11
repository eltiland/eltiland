package com.eltiland.ui.common.model;

import com.eltiland.model.user.User;
import org.apache.wicket.model.ResourceModel;

import javax.persistence.DiscriminatorValue;

/**
 * Create resource key for {@link User} class in next format:
 * {@code user.getDiscriminatorValue() + } {@link UserClassModel#SUFFIX}.
 *
 * @author Alexander Litvinenko
 */
public class UserClassModel extends ResourceModel {

    private static final String SUFFIX = "Label";

    /**
     * Constructor
     *
     * @param user user to extract class
     */
    public UserClassModel(User user) {
        super(user.getClass().getAnnotation(DiscriminatorValue.class).value() + SUFFIX);
    }

    /**
     * Constructor
     *
     * @param user         user to extract class
     * @param defaultValue value that will be returned if resource does not exist
     */
    public UserClassModel(User user, String defaultValue) {
        super(user.getClass().getSimpleName() + SUFFIX, defaultValue);
    }
}
