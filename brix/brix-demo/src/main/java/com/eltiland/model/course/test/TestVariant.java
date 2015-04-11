package com.eltiland.model.course.test;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Variant for tests.
 */
@Entity
@Table(name = "course_test_variant", schema = "public")
public class TestVariant extends TestEntity implements Serializable {
    private String value;
    private int number;
    private int orderNumber;
    private TestCourseItem item;
    private TestQuestion question;

    private static final int VALUE_LEN = 1024;

    @Column(name = "value", nullable = false, length = VALUE_LEN)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public TestCourseItem getItem() {
        return item;
    }

    public void setItem(TestCourseItem item) {
        this.item = item;
    }

    @Override
    @Transient
    public String getTextValue() {
        return getValue();
    }

    @Override
    @Transient
    public void setTextValue(String value) {
        setValue(value);
    }

    @Override
    @Transient
    public TestCourseItem getTestItem() {
        return getItem();
    }

    @Override
    @Transient
    public void setTestItem(TestCourseItem item) {
        setItem(item);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question")
    public TestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(TestQuestion question) {
        this.question = question;
    }

    @Column(name = "order_number")
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
