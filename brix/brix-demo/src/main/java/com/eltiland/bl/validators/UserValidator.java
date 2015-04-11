package com.eltiland.bl.validators;

import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User validator.
 */
@Component
public class UserValidator {

    @Autowired
    private UserManager userManager;

    /**
     * Validates parameters for user .
     * 1. User must be not null.
     * 2. Name, email must not be empty .
     * 3. Teacher must have a PEI.
     * 5. Email for user must be unique.
     */
    public void validateCreateParams(User user) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null!");
        } else if (user.getName() == null) {
            throw new UserException("Name cannot be null!");
        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserException("Email cannot be empty!");
        } else if (!validateUniqueEmail(user.getEmail(), user.getId())) {
            throw new UserException("Email must be unique!");
        }
    }

    /**
     * Validates uniques of email.
     * Used only for user creating - email must be absolutely unique.
     *
     * @param email email of current PEI
     * @param id    id of current PEI
     * @return TRUE, if validation is successful
     */
    public boolean validateUniqueEmail(String email, Long id) {
        User user = userManager.getUserByEmail(email);
        if (id == null) {
            return (user == null);
        } else {
            return user == null || user.getId().equals(id);
        }
    }
}
