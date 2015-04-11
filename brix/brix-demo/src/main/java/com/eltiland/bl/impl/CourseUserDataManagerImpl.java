package com.eltiland.bl.impl;

import com.eltiland.bl.CourseUserDataManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseUserData;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Component
public class CourseUserDataManagerImpl extends ManagerImpl implements CourseUserDataManager {

    @Autowired
    private GenericManager genericManager;

    private static final String COMPANY = "Место работы";
    private static final String JOB = "Должность";
    private static final String ADDRESS = "Почтовый адрес";
    private static final String PHONE = "Телефон";
    private static final String EXPERIENCE = "Стаж";


    @Override
    @Transactional
    public void createStandart(Course course) throws EltilandManagerException {
        CourseUserData[] datas = new CourseUserData[5];
        for (int i = 0; i < 5; i++) {
            datas[i] = new CourseUserData();
        }
        fillData(datas[0], course, CourseUserData.Type.COMPANY);
        fillData(datas[1], course, CourseUserData.Type.JOB);
        fillData(datas[2], course, CourseUserData.Type.ADDRESS);
        fillData(datas[3], course, CourseUserData.Type.PHONE);
        fillData(datas[4], course, CourseUserData.Type.EXPERIENCE);
        try {
            for (int i = 0; i < 5; i++) {
                genericManager.saveNew(datas[i]);
            }
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation during creating user data", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseUserData getCourseData(Course course, CourseUserData.Type type) {
        Criteria criteria = getCurrentSession().createCriteria(CourseUserData.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("type", type));
        return (CourseUserData) criteria.uniqueResult();
    }

    private void fillData(CourseUserData data, Course course, CourseUserData.Type type) {
        data.setCourse(course);
        data.setActive(false);
        data.setRequired(false);
        data.setCaption(getCaption(type));
        data.setType(type);
    }

    private String getCaption(CourseUserData.Type type) {
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

