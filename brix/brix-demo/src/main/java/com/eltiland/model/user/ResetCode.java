package com.eltiland.model.user;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.Date;

/**
 * Reset password information Entity.
 *
 * @author Aleksey Plotnikov
 */
@Entity
@Table(name = "resetcode", schema = "public")
public class ResetCode extends AbstractIdentifiable {
    private String code;
    private User user;
    private Date endingDate;

    public final static String RESET_PASS_LETTER = "templates/passwordReset.fo";

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "resetcode")
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
