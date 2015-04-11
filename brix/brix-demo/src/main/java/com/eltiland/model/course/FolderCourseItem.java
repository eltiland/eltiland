package com.eltiland.model.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Folder course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("FOLDER")
public class FolderCourseItem extends CourseItem {
    @SpringBean
    private GenericManager genericManager;

    private Set<CoursePaidInvoice> invoices = new HashSet<>(0);

    @Override
    @Transient
    public boolean getAllowsChildren() {
        return true;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<CoursePaidInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(Set<CoursePaidInvoice> invoices) {
        this.invoices = invoices;
    }

    @Transient
    public Course getParentCourse() {
        Injector.get().inject(this);
        Course result = this.getCourse();
        FolderCourseItem folder = this;
        while (result == null) {
            genericManager.initialize(folder, folder.getParentItem());
            folder = (FolderCourseItem) folder.getParentItem();
            result = folder.getCourse();
        }
        return result;
    }
}
