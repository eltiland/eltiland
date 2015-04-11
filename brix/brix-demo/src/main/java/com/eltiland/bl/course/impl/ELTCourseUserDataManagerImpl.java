package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseUserDataManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseUserDataValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseUserData;
import com.eltiland.model.course2.listeners.UserDataStatus;
import com.eltiland.model.course2.listeners.UserDataType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Course manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseUserDataManagerImpl extends ManagerImpl implements ELTCourseUserDataManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private CourseUserDataValidator courseUserDataValidator;

    private static final String COMPANY = "Место работы";
    private static final String JOB = "Должность";
    private static final String ADDRESS = "Почтовый адрес";
    private static final String PHONE = "Телефон";
    private static final String EXPERIENCE = "Стаж";


    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseUserData create(ELTCourseUserData data) throws CourseException {
        courseUserDataValidator.isCourseUserDataValid(data);
        try {
            return genericManager.saveNew(data);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_USERDATA_CREATE);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseUserData update(ELTCourseUserData data) throws CourseException {
        courseUserDataValidator.isCourseUserDataValid(data);
        try {
            return genericManager.update(data);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_USERDATA_UPDATE);
        }
    }

    @Override
    public void createStandart(ELTCourse course) throws CourseException {
        ELTCourseUserData[] datas = new ELTCourseUserData[5];
        for (int i = 0; i < 5; i++) {
            datas[i] = new ELTCourseUserData();
        }
        fillData(datas[0], course, UserDataType.ADDRESS);
        fillData(datas[1], course, UserDataType.COMPANY);
        fillData(datas[2], course, UserDataType.PHONE);
        fillData(datas[3], course, UserDataType.EXPERIENCE);
        fillData(datas[4], course, UserDataType.JOB);
        for (int i = 0; i < 5; i++) {
            create(datas[i]);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ELTCourseUserData get(ELTCourse course, UserDataType type) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseUserData.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("type", type));
        return (ELTCourseUserData) criteria.uniqueResult();
    }

    private void fillData(ELTCourseUserData data, ELTCourse course, UserDataType type) {
        data.setCourse(course);
        data.setStatus(UserDataStatus.NO);
        data.setCaption(getCaption(type));
        data.setType(type);
    }

    private String getCaption(UserDataType type) {
        switch (type) {
            case COMPANY:
                return COMPANY;
            case JOB:
                return JOB;
            case ADDRESS:
                return ADDRESS;
            case PHONE:
                return PHONE;
            case EXPERIENCE:
                return EXPERIENCE;
            default:
                return StringUtils.EMPTY;
        }
    }
}
