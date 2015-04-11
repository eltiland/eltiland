package com.eltiland.model.course;

import com.eltiland.model.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Folder course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("TASK")
public class TaskCourseItem extends ElementCourseItem {
    private Set<User> usersCompletes = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "completedTasks")
    public Set<User> getUsersCompletes() {
        return usersCompletes;
    }

    public void setUsersCompletes(Set<User> usersCompletes) {
        this.usersCompletes = usersCompletes;
    }
}
