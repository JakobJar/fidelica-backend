package org.fidelica.backend.repository.user;

import org.fidelica.backend.user.group.Group;

import java.util.Collection;

public interface GroupRepository {

    void create(Group group);

    Collection<Group> findAll();
}
