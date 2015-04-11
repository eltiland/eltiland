package com.eltiland.model;

import com.eltiland.model.file.File;
import com.eltiland.model.search.PeiApprovedFilterFactory;
import com.eltiland.model.user.Child;
import org.hibernate.search.annotations.*;
import org.hibernate.search.bridge.builtin.BooleanBridge;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for Preschool educational institution profile editing/creating (PEI).
 *
 * @author Aleksey Plotnikov
 */
@Entity
@Indexed
@Table(name = "pei", schema = "public",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@FullTextFilterDefs({
        @FullTextFilterDef(name = "peiApprovedFilterFactory", impl = PeiApprovedFilterFactory.class)
})
public class Pei extends AbstractIdentifiable implements IWithAvatar {

    private String name;
    private String address;
    private String email;
    private String manager;
    private String description;
    private String phone;
    private Boolean familyPresent;
    private Boolean consultationPresent;
    private String website;
    private String groupcount;
    private File avatar;

    @Column(name = "name", nullable = false)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "address", nullable = false)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "email", nullable = false, unique = true)
    @Field
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "manager")
    @Field
    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    @Column(name = "description")
    @Field
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "phone")
    @Field
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "familypresent")
    public Boolean getFamilyPresent() {
        return familyPresent;
    }

    public void setFamilyPresent(Boolean familyPresent) {
        this.familyPresent = familyPresent;
    }

    @Column(name = "conspresent")
    public Boolean getConsultationPresent() {
        return consultationPresent;
    }

    public void setConsultationPresent(Boolean consultationPresent) {
        this.consultationPresent = consultationPresent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar__id", nullable = false)
    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    @Column(name = "website")
    @Field
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name = "groupcount")
    public String getGroupCount() {
        return groupcount;
    }

    public void setGroupCount(String groupCount) {
        this.groupcount = groupCount;
    }
}
