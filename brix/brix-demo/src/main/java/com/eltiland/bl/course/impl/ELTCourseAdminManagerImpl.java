package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseAdminManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseAdminValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course2.CourseAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Course admin manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseAdminManagerImpl extends ManagerImpl implements ELTCourseAdminManager {
    @Autowired
    private CourseAdminValidator courseAdminValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public CourseAdmin create(CourseAdmin admin) throws CourseException {
        courseAdminValidator.isCourseAdminValid(admin);

        try {
            return genericManager.saveNew(admin);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_COURSEADMIN_CREATE, e);
        }
    }
}
