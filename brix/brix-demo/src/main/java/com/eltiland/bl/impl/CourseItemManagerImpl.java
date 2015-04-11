package com.eltiland.bl.impl;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.test.TestQuestionManager;
import com.eltiland.bl.test.TestResultManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course.*;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.model.course.test.TestVariant;
import com.eltiland.model.file.File;
import com.eltiland.model.google.GoogleDriveFile;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 */
@Component
public class CourseItemManagerImpl extends ManagerImpl implements CourseItemManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private TestQuestionManager testQuestionManager;
    @Autowired
    private TestResultManager testResultManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private GoogleDriveManager googleDriveManager;

    @Override
    @Transactional
    public CourseItem createCourseItem(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException {
        try {
            int index = getNextIndex(item, kind);
            item.setIndex(index);

            if (item instanceof GoogleCourseItem) {
                GoogleDriveFile file = null;
                if (item instanceof DocumentCourseItem) {
                    file = googleDriveManager.createEmptyDoc(item.getName(), GoogleDriveFile.TYPE.DOCUMENT);
                } else if (item instanceof PresentationCourseItem) {
                    file = googleDriveManager.createEmptyDoc(item.getName(), GoogleDriveFile.TYPE.PRESENTATION);
                }
                ((GoogleCourseItem) item).setDriveFile(file);
            }

            genericManager.saveNew(item);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when creating course item", e);
        } catch (GoogleDriveException e) {
            throw new EltilandManagerException("Errow when creating google document", e);
        }
        return item;
    }

    @Override
    public void deleteCourseItem(CourseItem item,
                                 Course.CONTENT_KIND kind, boolean isMove) throws EltilandManagerException {
        genericManager.initialize(item, item.getChildren());

        genericManager.initialize(item, item.getFiles());
        for (File file : fileManager.getFilesOfCourseItem(item)) {
            try {
                fileManager.deleteFile(file);
            } catch (FileException e) {
                throw new EltilandManagerException(e.getMessage(), e);
            }
        }

        if (item instanceof TestCourseItem) {
            deleteTestItems((TestCourseItem) item);
        }

        if (item instanceof GoogleCourseItem) {
            try {
                deleteDocItem((GoogleCourseItem) item);
            } catch (ConstraintException | GoogleDriveException e) {
                throw new EltilandManagerException("Errow when deleting google document", e);
            }
        }

        if (item instanceof VideoCourseItem) {
            deleteVideoItems((VideoCourseItem) item);
        }

        for (CourseItem child : item.getChildren()) {
            deleteCourseItem(child, kind, false);
        }

        if (isMove) {
            int index = item.getIndex();
            int maxIndex;
            if (item.getParentItem() == null) {
                genericManager.initialize(item, item.getCourse());
                Course course = item.getCourse();
                if (kind.equals(Course.CONTENT_KIND.DEMO)) {
                    genericManager.initialize(course, course.getDemoVersion());
                    maxIndex = course.getDemoVersion().size();
                } else {
                    genericManager.initialize(course, course.getFullVersion());
                    maxIndex = course.getFullVersion().size();
                }
                for (int i = index + 1; i < maxIndex; i++) {
                    moveCourseItemUp(getTopLevelElementByIndex(course, kind, i), kind);
                }
            } else {
                genericManager.initialize(item, item.getParentItem());
                CourseItem parentItem = item.getParentItem();
                maxIndex = parentItem.getChildCount();
                for (int i = index + 1; i < maxIndex; i++) {
                    moveCourseItemUp((CourseItem) item.getParentItem().getChildAt(i), kind);
                }
            }
        }

        CourseItem dItem = genericManager.getObject(CourseItem.class, item.getId());
        deleteItem(dItem);
    }

    @Transactional
    private void deleteItem(CourseItem item) throws EltilandManagerException {
        genericManager.delete(item);
    }


    @Transactional
    private void deleteTestItems(TestCourseItem item) throws EltilandManagerException {
        genericManager.initialize(item, item.getVariants());
        genericManager.initialize(item, item.getQuestions());
        genericManager.initialize(item, item.getResults());
        for (TestResult result : item.getResults()) {
            genericManager.delete(result);
        }
        for (TestVariant variant : item.getVariants()) {
            genericManager.delete(variant);
        }
        for (TestQuestion question : testQuestionManager.getSortedTopLevelList(item)) {
            deleteTestQuestionItems(question);
            genericManager.delete(question);
        }
    }

    @Transactional
    private void deleteVideoItems(VideoCourseItem item) throws EltilandManagerException {
        genericManager.initialize(item, item.getVideoItems());
        for (CourseVideoItem video : item.getVideoItems()) {
            genericManager.delete(video);
        }
    }

    @Transactional
    private void deleteDocItem(GoogleCourseItem item) throws ConstraintException, GoogleDriveException {
        genericManager.initialize(item, item.getDriveFile());

        //GoogleDriveFile driveFile = item.getDriveFile();
        item.setDriveFile(null);
        genericManager.update(item);

        //googleDriveManager.deleteFile(driveFile);
    }

    @Transactional
    private void deleteTestQuestionItems(TestQuestion testQuestion) throws EltilandManagerException {
        genericManager.initialize(testQuestion, testQuestion.getChildren());
        genericManager.initialize(testQuestion, testQuestion.getResults());
        genericManager.initialize(testQuestion, testQuestion.getVariants());
        for (TestQuestion question : testQuestion.getChildren()) {
            genericManager.delete(question);
        }
        for (TestResult result : testQuestion.getResults()) {
            testResultManager.deleteTestResult(result);
        }
        for (TestVariant variant : testQuestion.getVariants()) {
            genericManager.delete(variant);
        }
    }

    @Override
    @Transactional
    public void updateCourseItem(CourseItem item) throws EltilandManagerException {
        try {
            genericManager.update(item);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating course item", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseItem getCourseItemById(Long id) {
        Query query = getCurrentSession().createQuery("select courseItem from CourseItem as courseItem " +
                "left join fetch courseItem.parentItem as parentItem " +
                "where courseItem.id = :id")
                .setParameter("id", id);
        return (CourseItem) query.uniqueResult();
    }

    @Override
    @Transactional
    public void moveCourseItemUp(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException {
        int index = item.getIndex();
        if (index != 0) {
            CourseItem moveItem;

            if (item.getParentItem() == null) {
                moveItem = getTopLevelElementByIndex(item.getCourse(), kind, index - 1);
            } else {
                CourseItem parentItem = item.getParentItem();
                moveItem = (CourseItem) parentItem.getChildAt(index - 1);
            }

            moveItem.setIndex(index);
            item.setIndex(index - 1);

            updateCourseItem(moveItem);
            updateCourseItem(item);
        }
    }

    @Override
    @Transactional
    public void moveCourseItemDown(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException {
        int count = getItemsCountInLevel(item);
        int index = item.getIndex();
        if ((index + 1) < count) {
            CourseItem moveItem;

            if (item.getParentItem() == null) {
                moveItem = getTopLevelElementByIndex(item.getCourse(), kind, index + 1);
            } else {
                CourseItem parentItem = item.getParentItem();
                moveItem = (CourseItem) parentItem.getChildAt(index + 1);
            }

            moveItem.setIndex(index);
            item.setIndex(index + 1);

            updateCourseItem(moveItem);
            updateCourseItem(item);
        }
    }

    @Override
    public CourseItem getTopLevelElementByIndex(Course course, Course.CONTENT_KIND kind, int index) {
        Set<CourseItem> items =
                kind.equals(Course.CONTENT_KIND.DEMO) ? course.getDemoVersion() : course.getFullVersion();
        for (CourseItem item : items) {
            if (item.getIndex() == index) {
                return item;
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public TestCourseItem initializeTestItem(TestCourseItem item) {
        Query query = getCurrentSession().createQuery("select testItem from TestCourseItem as testItem " +
                "left join fetch testItem.results as results " +
                "left join fetch testItem.variants as variants " +
                "where testItem.id = :id")
                .setParameter("id", item.getId());
        return (TestCourseItem) query.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestQuestion> getTopLevelQuestions(TestCourseItem item, TestQuestion exceptQuestion) {
        return getTopLevelQuestions(item, exceptQuestion, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestQuestion> getTopLevelQuestions(
            TestCourseItem item, TestQuestion exceptQuestion, boolean isSortByNumber) {
        Criteria criteria = getCurrentSession().createCriteria(TestQuestion.class);
        criteria.add(Restrictions.eq("item", item));
        criteria.add(Restrictions.isNull("parentItem"));
        if (exceptQuestion != null) {
            criteria.add(Restrictions.ne("id", exceptQuestion.getId()));
        }
        if (isSortByNumber) {
            criteria.addOrder(Order.asc("number"));
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getItemsCountInLevel(CourseItem item) {
        if (item.getParentItem() == null) {
            Criteria criteria = getCurrentSession().createCriteria(CourseItem.class);
            criteria.add(Restrictions.isNull("parentItem"));
            if (item.getCourseDemo() != null) {
                criteria.add(Restrictions.eq("courseDemo", item.getCourseDemo()));
            } else if (item.getCourseFull() != null) {
                criteria.add(Restrictions.eq("courseFull", item.getCourseFull()));
            }
            return criteria.list().size();
        } else {
            genericManager.initialize(item, item.getParentItem());
            genericManager.initialize(item.getParentItem(), item.getParentItem().getChildren());
            return item.getParentItem().getChildren().size();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseItem getItemByParentIdAndName(Long id, String name) {
        Criteria criteria = getCurrentSession().createCriteria(CourseItem.class);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("parentItem.id", id));
        return (CourseItem) criteria.uniqueResult();
    }

    private int getMaxIndex(Course course, Course.CONTENT_KIND kind) {
        int max = -1;
        Set<CourseItem> items;
        if (kind.equals(Course.CONTENT_KIND.DEMO)) {
            genericManager.initialize(course, course.getDemoVersion());
            items = course.getDemoVersion();
        } else {
            genericManager.initialize(course, course.getFullVersion());
            items = course.getFullVersion();
        }
        for (CourseItem item : items) {
            int index = item.getIndex();
            if (index > max) {
                max = index;
            }
        }
        return max;
    }

    private int getMaxIndex(CourseItem item) {
        int max = -1;
        for (CourseItem child : item.getChildren()) {
            int index = child.getIndex();
            if (index > max) {
                max = index;
            }
        }
        return max;
    }

    private int getNextIndex(CourseItem item, Course.CONTENT_KIND kind) {
        if (item.getParent() == null) { // top level
            Course course = item.getCourse();
            return getMaxIndex(course, kind) + 1;
        } else {
            CourseItem parent = item.getParentItem();
            parent = genericManager.getObject(CourseItem.class, parent.getId());
            return getMaxIndex(parent) + 1;
        }
    }
}