package com.eltiland.model.course2.content;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.model.file.File;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Course item entity.
 *
 * @author Alekxsey Plotnikov.
 */
@Entity
@Table(name = "item", schema = "course")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_prototype", discriminatorType = DiscriminatorType.STRING)
public abstract class ELTCourseItem extends AbstractIdentifiable {
    private ELTCourseBlock block;
    private Long index;
    private String name;
    private boolean control;
    private ELTGroupCourseItem parent;

    private Set<File> files = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block")
    public ELTCourseBlock getBlock() {
        return block;
    }

    public void setBlock(ELTCourseBlock block) {
        this.block = block;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @Column(name = "name", nullable = false, length = 128)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "control", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courseItem")
    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public ELTGroupCourseItem getParent() {
        return parent;
    }

    public void setParent(ELTGroupCourseItem parent) {
        this.parent = parent;
    }
}
