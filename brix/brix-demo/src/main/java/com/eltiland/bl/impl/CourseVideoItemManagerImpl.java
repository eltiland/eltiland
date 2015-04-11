package com.eltiland.bl.impl;

import com.eltiland.bl.CourseVideoItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.CourseVideoItem;
import com.eltiland.model.course.VideoCourseItem;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CourseVideoItemManagerImpl extends ManagerImpl implements CourseVideoItemManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public CourseVideoItem createVideoItem(CourseVideoItem item) throws EltilandManagerException {
        int index = getCurrentMaxNumber(item.getItem());
        item.setIndex(index + 1);
        try {
            return genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create item - constraint exception", e);
        }
    }

    @Override
    public void deleteVideoItem(CourseVideoItem item) throws EltilandManagerException {
        int currentNumber;
        int oldNumber = item.getIndex();

        genericManager.initialize(item, item.getItem());
        VideoCourseItem videoItem = item.getItem();
        genericManager.initialize(videoItem, videoItem.getVideoItems());
        int count = videoItem.getVideoItems().size();
        for (currentNumber = oldNumber; currentNumber < (count - 1); currentNumber++) {
            moveUp(getItemByIndex(item.getItem(), currentNumber + 1));
        }

        deleteItem(item);
    }

    @Transactional
    private void deleteItem(CourseVideoItem item) throws EltilandManagerException {
        genericManager.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseVideoItem> getItemList(
            VideoCourseItem item, int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(CourseVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        if (sProperty != null) {
            criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        }
        criteria.setMaxResults(count);
        criteria.setFirstResult(index);
        return criteria.list();
    }

    @Override
    @Transactional
    public int getItemCount(VideoCourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(CourseVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        return criteria.list().size();
    }

    @Override
    @Transactional
    public void moveUp(CourseVideoItem item) throws EltilandManagerException {
        genericManager.initialize(item, item.getItem());

        int oldNumber = item.getIndex();
        if (oldNumber != 0) {
            CourseVideoItem courseVideoItem = getItemByIndex(item.getItem(), oldNumber - 1);
            courseVideoItem.setIndex(oldNumber);
            item.setIndex(oldNumber - 1);
            try {
                genericManager.update(courseVideoItem);
                genericManager.update(item);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Cannot create entity, constraint violation!", e);
            }
        }
    }

    @Override
    @Transactional
    public void moveDown(CourseVideoItem item) throws EltilandManagerException {
        genericManager.initialize(item, item.getItem());

        int oldNumber = item.getIndex();
        if (oldNumber != (getItemCount(item.getItem()) - 1)) {
            CourseVideoItem nextItem = getItemByIndex(item.getItem(), oldNumber + 1);
            nextItem.setIndex(oldNumber);
            item.setIndex(oldNumber + 1);
            try {
                genericManager.update(nextItem);
                genericManager.update(item);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Cannot create entity, constraint violation!", e);
            }
        }
    }

    @Transactional(readOnly = true)
    private int getCurrentMaxNumber(VideoCourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(CourseVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        criteria.setProjection(Projections.max("index"));
        return (criteria.uniqueResult() != null) ? (Integer) criteria.uniqueResult() : -1;
    }

    @Transactional(readOnly = true)
    private CourseVideoItem getItemByIndex(VideoCourseItem item, int index) {
        Criteria criteria = getCurrentSession().createCriteria(CourseVideoItem.class);
        criteria.add(Restrictions.eq("item", item));
        criteria.add(Restrictions.eq("index", index));
        return (CourseVideoItem) criteria.uniqueResult();
    }
}