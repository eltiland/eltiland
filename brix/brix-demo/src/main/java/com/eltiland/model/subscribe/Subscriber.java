package com.eltiland.model.subscribe;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity for subscriber.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "subscriber", schema = "public")
public class Subscriber extends AbstractIdentifiable {
    private String email;
    private Date creationdate;
    private boolean disabled;
    private String unsubscribe;

    @Column(name = "email", nullable = false, length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "creationdate", nullable = false)
    public Date getCreationDate() {
        return creationdate;
    }

    public void setCreationDate(Date creationdate) {
        this.creationdate = creationdate;
    }

    @Column(name = "disabled", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Column(name = "unsubscribe", nullable = false, length = 10)
    public String getUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(String unsubscribe) {
        this.unsubscribe = unsubscribe;
    }
}
