package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseItemValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
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
public class ELTCourseItemManagerImpl extends ManagerImpl implements ELTCourseItemManager {

    @Autowired
    private CourseItemValidator courseItemValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseItem create(ELTCourseItem item) throws CourseException {
        courseItemValidator.isValid(item);
        try {
            return genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_ITEM_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseItem update(ELTCourseItem item) throws CourseException {
        courseItemValidator.isValid(item);
        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_ITEM_UPDATE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseItem> getItems(ELTCourseBlock block) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseItem.class);
        criteria.add(Restrictions.eq("block", block));
        criteria.addOrder(Order.asc("index"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseItem> getItems(ELTGroupCourseItem group) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseItem.class);
        criteria.add(Restrictions.eq("parent", group));
        criteria.addOrder(Order.asc("index"));
        return criteria.list();
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveUp(ELTCourseItem item) throws CourseException {
        Long index = item.getIndex();
        genericManager.initialize(item, item.getBlock());
        genericManager.initialize(item, item.getParent());
        if (index != 0) {
            ELTCourseItem moveItem = getItemByIndex(index - 1, item.getBlock(), item.getParent());
            moveItem.setIndex(index);
            item.setIndex(index - 1);
            update(moveItem);
            update(item);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveDown(ELTCourseItem item) throws CourseException {
        Long index = item.getIndex();
        genericManager.initialize(item, item.getBlock());
        genericManager.initialize(item, item.getParent());
        boolean inGroup = item.getParent() != null;
        if (inGroup) {
            genericManager.initialize(item.getParent(), item.getParent().getItems());
        } else {
            genericManager.initialize(item.getBlock(), item.getBlock().getItems());
        }
        int count = inGroup ? item.getParent().getItems().size() : item.getBlock().getItems().size();
        if (index < count) {
            ELTCourseItem moveItem = getItemByIndex(index + 1, item.getBlock(), item.getParent());
            moveItem.setIndex(index);
            item.setIndex(index + 1);
            update(moveItem);
            update(item);
        }
    }

    @Override
    public ELTCourse getCourse(ELTCourseItem item) {
        genericManager.initialize(item, item.getBlock());
        ELTCourseBlock block = item.getBlock();
        if (block == null) {
            genericManager.initialize(item, item.getParent());
            genericManager.initialize(item.getParent(), item.getParent().getBlock());
            block = item.getParent().getBlock();
        }
        genericManager.initialize(block, block.getCourse());
        genericManager.initialize(block, block.getDemoCourse());

        return block.getCourse() != null ? block.getCourse() : block.getDemoCourse();
    }

    @Transactional(readOnly = true)
    private ELTCourseItem getItemByIndex(Long index, ELTCourseBlock block, ELTGroupCourseItem group) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseItem.class);
        if (block != null) {
            criteria.add(Restrictions.eq("block", block));
        } else {
            criteria.add(Restrictions.eq("parent", group));
        }
        criteria.add(Restrictions.eq("index", index));
        return (ELTCourseItem) criteria.uniqueResult();
    }
}
