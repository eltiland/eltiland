package com.eltiland.model.user;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.Date;

/**
 * Confirmation information Entity.
 *
 * @author Aleksey Plotnikov
 */
@Entity
@Table(name = "confirmation", schema = "public")
public class Confirmation extends AbstractIdentifiable {
    private String code;
    private User user;
    private Date endingDate;

    public final static String PEI_TEMPLATE_LETTER = "templates/confirmationPEIMessage.fo";
    public final static String USER_TEMPLATE_LETTER = "templates/confirmationUserMessage.fo";
    public final static String SIMPLE_USER_TEMPLATE_LETTER = "templates/confirmationSimpleUserMessage.fo";
    public final static String TEACHER_TEMPLATE_LETTER = "templates/confirmationTeacherMessage.fo";
    public final static String TEACHER_TEMPLATE_SECOND_LETTER = "templates/confirmationTeacherMessageSecond.fo";
    public final static String USER_NEXT_CHILD_LETTER = "templates/confirmationNextChildMessage.fo";

    @Column(name = "address", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "confirmation")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "endingDate")
    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }
}
