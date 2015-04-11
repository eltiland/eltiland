package com.eltiland.model.subscribe;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity for subscribe email.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "email", schema = "public")
public class Email extends AbstractIdentifiable {
    private String header;
    private String content;
    private boolean status;
    private Date senddate;

    @Column(name = "header", nullable = false, length = 100)
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "status", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name = "senddate")
    public Date getSendDate() {
        return senddate;
    }

    public void setSendDate(Date senddate) {
        this.senddate = senddate;
    }
}
