package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseBlockManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseBlockValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseBlockManagerImpl extends ManagerImpl implements ELTCourseBlockManager {

    @Autowired
    private CourseBlockValidator blockValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseBlock create(ELTCourseBlock block) throws CourseException {
        blockValidator.isCourseBlockValid(block);

        try {
            return genericManager.saveNew(block);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_BLOCK_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseBlock update(ELTCourseBlock block) throws CourseException {
        blockValidator.isCourseBlockValid(block);
        try {
            return genericManager.update(block);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_BLOCK_UPDATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveUp(ELTCourseBlock block) throws CourseException {
        int index = block.getIndex();

        genericManager.initialize(block, block.getCourse());
        genericManager.initialize(block, block.getDemoCourse());
        ContentStatus status = (block.getCourse() == null) ? ContentStatus.DEMO : ContentStatus.FULL;
        ELTCourse course = status.equals(ContentStatus.DEMO) ? block.getDemoCourse() : block.getCourse();

        if (index > 1) {
            ELTCourseBlock topBlock = getBlockByIndex(index - 1, course, status);
            topBlock.setIndex(index);
            block.setIndex(index - 1);
            update(topBlock);
            update(block);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveDown(ELTCourseBlock block) throws CourseException {
        int index = block.getIndex();

        genericManager.initialize(block, block.getCourse());
        genericManager.initialize(block, block.getDemoCourse());
        ContentStatus status = (block.getCourse() == null) ? ContentStatus.DEMO : ContentStatus.FULL;
        ELTCourse course = status.equals(ContentStatus.DEMO) ? block.getDemoCourse() : block.getCourse();
        int count = status.equals(ContentStatus.DEMO) ?
                block.getDemoCourse().getDemoContent().size() : block.getCourse().getContent().size();
        if (index < count) {
            ELTCourseBlock topBlock = getBlockByIndex(index + 1, course, status);
            topBlock.setIndex(index);
            block.setIndex(index + 1);
            update(topBlock);
            update(block);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseBlock> getSortedBlockList(ELTCourse course, ContentStatus status) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseBlock.class);
        if (status.equals(ContentStatus.DEMO)) {
            criteria.add(Restrictions.eq("demoCourse", course));
        } else {
            criteria.add(Restrictions.eq("course", course));
        }
        criteria.addOrder(Order.asc("index"));
        return criteria.list();
    }

    @Transactional(readOnly = true)
    private ELTCourseBlock getBlockByIndex(int index, ELTCourse course, ContentStatus status) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseBlock.class);
        if (status.equals(ContentStatus.DEMO)) {
            criteria.add(Restrictions.eq("demoCourse", course));
        } else {
            criteria.add(Restrictions.eq("course", course));
        }
        criteria.add(Restrictions.eq("index", index));
        return (ELTCourseBlock) criteria.uniqueResult();
    }
}
