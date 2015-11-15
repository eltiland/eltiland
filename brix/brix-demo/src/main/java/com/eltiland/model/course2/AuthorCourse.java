package com.eltiland.model.course2;

import com.eltiland.model.Countable;
import com.eltiland.model.course2.content.ELTCourseBlock;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Training course entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("AUTHOR")
public class AuthorCourse extends ELTCourse implements Countable {
    private Integer index;
    private Boolean module;
    private Set<ELTCourseBlock> demoContent = new HashSet<>(0);

    @Column(name = "index")
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "demoCourse")
    public Set<ELTCourseBlock> getDemoContent() {
        return demoContent;
    }

    public void setDemoContent(Set<ELTCourseBlock> demoContent) {
        this.demoContent = demoContent;
    }

    @Column(name = "module", nullable = false, columnDefinition = "boolean default TRUE")
    public Boolean isModule() {
        return module;
    }

    public void setModule(Boolean module) {
        this.module = module;
    }
}
