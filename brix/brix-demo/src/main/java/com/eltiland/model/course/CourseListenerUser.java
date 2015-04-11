package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Courselistener - User M:M relation table.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_listener_user", schema = "public")
public class CourseListenerUser extends AbstractIdentifiable {

    private CourseListener listener;
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener_id", nullable = false)
    public CourseListener getListener() {
        return listener;
    }

    public void setListener(CourseListener listener) {
        this.listener = listener;
    }
}
