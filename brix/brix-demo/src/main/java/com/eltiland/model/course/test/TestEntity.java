package com.eltiland.model.course.test;

import com.eltiland.model.AbstractIdentifiable;

/**
 * Test entity interface.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TestEntity extends AbstractIdentifiable {

    /**
     * @return Text value of entity.
     */
    public abstract String getTextValue();

    /**
     * Set text value of entity.
     *
     * @param value new text value.
     */
    public abstract void setTextValue(String value);

    /**
     * @return Test item.
     */
    public abstract TestCourseItem getTestItem();

    /**
     * Set test item.
     *
     * @param item new test item..
     */
    public abstract void setTestItem(TestCourseItem item);

    /**
     * @return Ordinal number of test entity in the test.
     */
    public abstract int getNumber();
}
