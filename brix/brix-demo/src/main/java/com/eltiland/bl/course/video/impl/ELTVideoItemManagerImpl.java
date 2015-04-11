package com.eltiland.bl.course.video.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.video.ELTVideoItemManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseVideoValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoItem;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Video item manager for video course item.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTVideoItemManagerImpl extends ManagerImpl implements ELTVideoItemManager {

    @Autowired
    private CourseVideoValidator courseVideoValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTVideoItem create(ELTVideoItem item) throws CourseException {
        courseVideoValidator.isValid(item);

        try {
            return genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_CREATE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTVideoItem update(ELTVideoItem item) throws CourseException {
        courseVideoValidator.isValid(item);

        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_UPDATE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void delete(ELTVideoItem item) throws CourseException {
        try {
            genericManager.delete(item);
        } catch (EltilandManagerException e) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_DELETE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveUp(ELTVideoItem item) throws CourseException {
        genericManager.initialize(item, item.getItem());

        Long index = item.getIndex();
        if (index != 0) {
            ELTVideoItem moveItem = getItemByIndex(item.getItem(), index - 1);
            moveItem.setIndex(index);
            item.setIndex(index - 1);
            update(item);
            update(moveItem);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void moveDown(ELTVideoItem item) throws CourseException {
        genericManager.initialize(item, item.getItem());
        genericManager.initialize(item.getItem(), item.getItem().getItems());

        Long index = item.getIndex();
        if (index < item.getItem().getItems().size() - 1) {
            ELTVideoItem moveItem = getItemByIndex(item.getItem(), index + 1);
            moveItem.setIndex(index);
            item.setIndex(index + 1);
            update(moveItem);
            update(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTVideoItem> getItems(
            ELTVideoCourseItem item, int index, int count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(ELTVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCount(ELTVideoCourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(ELTVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        return criteria.list().size();
    }

    @Transactional(readOnly = true)
    private ELTVideoItem getItemByIndex(ELTVideoCourseItem item, Long index) {
        Criteria criteria = getCurrentSession().createCriteria(ELTVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        criteria.add(Restrictions.eq("index", index));
        return (ELTVideoItem) criteria.uniqueResult();
    }
}
