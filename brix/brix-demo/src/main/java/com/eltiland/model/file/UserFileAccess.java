package com.eltiland.model.file;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;

/**
 * User / UserFile M-M relation table.
 */
@Entity
@Table(name = "user_file_access", schema = "public")
public class UserFileAccess extends AbstractIdentifiable {
    private User client;
    private UserFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    public UserFile getFile() {
        return file;
    }

    public void setFile(UserFile file) {
        this.file = file;
    }
}
