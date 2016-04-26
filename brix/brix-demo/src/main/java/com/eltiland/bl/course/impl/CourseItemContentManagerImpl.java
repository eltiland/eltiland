package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.CourseItemContentManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.CourseItemContent;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Course item print statistics manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseItemContentManagerImpl extends ManagerImpl implements CourseItemContentManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public CourseItemContent create(CourseItemContent item) throws CourseException {
        try {
            item = genericManager.saveNew(item);
            return item;
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_CONTENT_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public CourseItemContent update(CourseItemContent item) throws CourseException {
        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_CONTENT_UPDATE, e);
        }
    }
}
