package com.eltiland.bl.course.audio.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.audio.ELTAudioItemManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.AudioItemValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.audio.ELTAudioCourseItem;
import com.eltiland.model.course2.content.audio.ELTAudioItem;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Video item manager for video course item.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTAudioItemManagerImpl extends ManagerImpl implements ELTAudioItemManager {

    @Autowired
    private AudioItemValidator audioItemValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTAudioItem create(ELTAudioItem item) throws CourseException {
        audioItemValidator.isValid(item);

        try {
            genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_AUDIO_ITEM_CREATE, e);
        }

        return item;
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTAudioItem update(ELTAudioItem item) throws CourseException {
        audioItemValidator.isValid(item);

        try {
            genericManager.update(item);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_AUDIO_ITEM_UPDATE, e);
        }

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public ELTAudioItem get(ELTAudioCourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(ELTAudioItem.class);
        criteria.add(Restrictions.eq("item", item));
        return (ELTAudioItem) criteria.uniqueResult();
    }
}
